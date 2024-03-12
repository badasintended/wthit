package mcp.mobius.waila.jaded.j2w;

import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import com.google.common.base.CaseFormat;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginDiscoverer;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import org.apache.commons.lang3.ClassUtils;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

public class JPluginDiscoverer implements IPluginDiscoverer {

    public static final ResourceLocation ID = Waila.id("jade_compat");

    @Override
    public ResourceLocation getDiscovererId() {
        return ID;
    }

    @Override
    public void discover(Candidates candidates) {
        if (!ModInfo.get("jade").isPresent()) return;

        candidates.add(WailaConstants.MOD_ID, new ResourceLocation("jade", "pre"), IPluginInfo.Side.BOTH, List.of(), true, JPreWailaPlugin::new);

        for (var info : ModList.get().getModFiles()) {
            var modFile = info.getFile();
            var modId = info.getMods().get(0).getModId();
            var hasPluginsJson = Files.exists(modFile.findResource("waila_plugins.json")) || Files.exists(modFile.findResource("wthit_plugins.json"));

            for (var annotation : modFile.getScanResult().getAnnotations()) {
                if (annotation.annotationType().getClassName().equals(WailaPlugin.class.getName())) {
                    var required = (String) annotation.annotationData().get("value");

                    var className = annotation.memberName();
                    var shortClassName = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(ClassUtils.getShortClassName(className));
                    var pluginId = new ResourceLocation(modId, Objects.requireNonNull(shortClassName));
                    var hasDisabler = false; // TODO

                    candidates.add(
                        modId, pluginId,
                        IPluginInfo.Side.BOTH, required != null ? List.of(required) : List.of(),
                        !(modId.equals("jade") || hasPluginsJson || hasDisabler),
                        () -> new JWailaPlugin((IWailaPlugin) Class.forName(className).getConstructor().newInstance()));
                }
            }
        }

        candidates.add(WailaConstants.MOD_ID, new ResourceLocation("jade", "post"), IPluginInfo.Side.BOTH, List.of(), true, JPostWailaPlugin::new);
    }

}
