package mcp.mobius.waila.plugin;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.util.CachedSupplier;
import mcp.mobius.waila.util.Log;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.resources.ResourceLocation;

public class PluginInfo implements IPluginInfo {

    private static final Log LOG = Log.create();
    private static final ResourceLocation CORE = Waila.id("core");

    private static final IJsonConfig<Map<ResourceLocation, Boolean>> TOGGLE = IJsonConfig.of(new TypeToken<Map<ResourceLocation, Boolean>>() {})
        .file(WailaConstants.NAMESPACE + "/" + "plugin_toggle")
        .factory(LinkedHashMap::new)
        .json5()
        .gson(new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create())
        .build();

    private static final Map<ResourceLocation, IPluginInfo> PLUGIN_ID_TO_PLUGIN_INFO = new LinkedHashMap<>();
    private static final CachedSupplier<Map<String, List<IPluginInfo>>> MOD_ID_TO_PLUGIN_INFOS = new CachedSupplier<>(() ->
        PLUGIN_ID_TO_PLUGIN_INFO.values().stream().collect(Collectors.groupingBy(p -> p.getModInfo().getId())));

    private final ModInfo modInfo;
    private final ResourceLocation pluginId;
    private final Side side;
    private final IWailaPlugin initializer;
    private final List<String> requiredModIds;
    private final boolean legacy;

    private boolean disabledOnServer;

    private PluginInfo(ModInfo modInfo, ResourceLocation pluginId, Side side, IWailaPlugin initializer, List<String> requiredModIds, boolean legacy) {
        this.modInfo = modInfo;
        this.pluginId = pluginId;
        this.side = side;
        this.initializer = initializer;
        this.requiredModIds = requiredModIds;
        this.legacy = legacy;
    }

    public static void register(String modId, String pluginIdStr, Side side, String initializerStr, List<String> required, boolean defaultEnabled, boolean legacy) {
        try {
            var rl = new ResourceLocation(pluginIdStr);
            if (PLUGIN_ID_TO_PLUGIN_INFO.containsKey(rl)) {
                LOG.error("Duplicate plugin id " + rl);
                return;
            }

            if (rl.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) {
                LOG.warn("Plugin " + initializerStr + " is using the default namespace " + rl);
            }

            var initializer = (IWailaPlugin) Class.forName(initializerStr).getConstructor().newInstance();
            PLUGIN_ID_TO_PLUGIN_INFO.put(rl, new PluginInfo(ModInfo.get(modId), rl, side, initializer, required, legacy));
            TOGGLE.get().putIfAbsent(rl, defaultEnabled);
        } catch (Throwable t) {
            LOG.error("Error creating instance of plugin " + pluginIdStr, t);
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

    @Override
    public boolean isEnabled() {
        if (disabledOnServer) return false;
        return isLocked() || TOGGLE.get().get(getPluginId());
    }

    public boolean isLegacy() {
        return legacy;
    }

    public boolean isLocked() {
        return pluginId.equals(CORE);
    }

    public boolean isDisabledOnServer() {
        return disabledOnServer;
    }

    public void setDisabledOnServer(boolean disabledOnServer) {
        this.disabledOnServer = disabledOnServer;
    }

    public void setEnabled(boolean enabled) {
        TOGGLE.get().put(getPluginId(), enabled);
    }

    public static void refresh() {
        TOGGLE.invalidate();
        getAll().forEach(it -> ((PluginInfo) it).disabledOnServer = false);
    }

    public static void saveToggleConfig() {
        TOGGLE.save();
    }

}
