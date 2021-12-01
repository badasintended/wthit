package mcp.mobius.waila.plugin;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Suppliers;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.util.CommonUtil;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.resources.ResourceLocation;

public class PluginInfo implements IPluginInfo {

    private static final Map<ResourceLocation, IPluginInfo> PLUGIN_ID_TO_PLUGIN_INFO = new LinkedHashMap<>();
    private static final Supplier<Map<String, List<IPluginInfo>>> MOD_ID_TO_PLUGIN_INFOS = Suppliers.memoize(() ->
        PLUGIN_ID_TO_PLUGIN_INFO.values().stream().collect(Collectors.groupingBy(p -> p.getModInfo().getId())));

    private final ModInfo modInfo;
    private final ResourceLocation pluginId;
    private final Side side;
    private final IWailaPlugin initializer;
    private final List<String> requiredModIds;

    private PluginInfo(ModInfo modInfo, ResourceLocation pluginId, Side side, IWailaPlugin initializer, List<String> requiredModIds) {
        this.modInfo = modInfo;
        this.pluginId = pluginId;
        this.side = side;
        this.initializer = initializer;
        this.requiredModIds = requiredModIds;

        PLUGIN_ID_TO_PLUGIN_INFO.put(pluginId, this);
    }

    public static void register(String modId, String pluginIdStr, Side side, String initializerStr, List<String> required) {
        try {
            IWailaPlugin initializer = (IWailaPlugin) Class.forName(initializerStr).getConstructor().newInstance();
            ResourceLocation rl = new ResourceLocation(pluginIdStr);
            PLUGIN_ID_TO_PLUGIN_INFO.put(rl, new PluginInfo(ModInfo.get(modId), rl, side, initializer, required));
        } catch (Throwable t) {
            CommonUtil.LOGGER.error("Error creating instance of plugin " + pluginIdStr, t);
        }
    }

    public static IPluginInfo get(ResourceLocation pluginId) {
        return PLUGIN_ID_TO_PLUGIN_INFO.get(pluginId);
    }

    public static Collection<IPluginInfo> getAllFromMod(String modId) {
        return MOD_ID_TO_PLUGIN_INFOS.get().get(modId);
    }

    public static Collection<IPluginInfo> getAll() {
        return PLUGIN_ID_TO_PLUGIN_INFO.values();
    }

    @Override
    public IModInfo getModInfo() {
        return modInfo;
    }

    @Override
    public ResourceLocation getPluginId() {
        return pluginId;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public IWailaPlugin getInitializer() {
        return initializer;
    }

    @Override
    public List<String> getRequiredModIds() {
        return requiredModIds;
    }

}
