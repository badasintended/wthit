package mcp.mobius.waila.plugin;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Suppliers;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.IWailaClientPlugin;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.util.CachedSupplier;
import mcp.mobius.waila.util.Log;
import mcp.mobius.waila.util.ModInfo;
import mcp.mobius.waila.util.ResourceLocationSerde;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class PluginInfo implements IPluginInfo {

    private static final Log LOG = Log.create();
    private static final ResourceLocation CORE = Waila.id("core");

    private static final IJsonConfig<Map<ResourceLocation, Boolean>> TOGGLE = IJsonConfig.of(new TypeToken<Map<ResourceLocation, Boolean>>() {})
        .file(WailaConstants.NAMESPACE + "/" + "plugin_toggle")
        .factory(LinkedHashMap::new)
        .json5()
        .gson(new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ResourceLocation.class, ResourceLocationSerde.INSTANCE)
            .create())
        .build();

    private static final Map<ResourceLocation, PluginInfo> PLUGIN_ID_TO_PLUGIN_INFO = new LinkedHashMap<>();
    private static final CachedSupplier<Map<String, List<PluginInfo>>> MOD_ID_TO_PLUGIN_INFOS = new CachedSupplier<>(() ->
        PLUGIN_ID_TO_PLUGIN_INFO.values().stream().collect(Collectors.groupingBy(p -> p.getModInfo().getId())));

    private static final IWailaPlugin EMPTY_INIT = registrar -> {};

    private final ModInfo modInfo;
    private final ResourceLocation pluginId;
    private final PluginSide side;
    private final List<String> requiredModIds;
    private final boolean legacy;

    private final @Nullable IWailaPlugin deprecatedInit;

    private final @Nullable Supplier<@Nullable IWailaCommonPlugin> common;
    private final @Nullable Supplier<@Nullable IWailaClientPlugin> client;

    private boolean disabledOnServer;

    private PluginInfo(
        ModInfo modInfo,
        ResourceLocation pluginId,
        PluginSide side,
        @Nullable IWailaPlugin deprecatedInit,
        List<String> requiredModIds,
        boolean legacy,
        @Nullable Supplier<@Nullable IWailaCommonPlugin> common,
        @Nullable Supplier<@Nullable IWailaClientPlugin> client
    ) {
        this.modInfo = modInfo;
        this.pluginId = pluginId;
        this.side = side;
        this.deprecatedInit = deprecatedInit;
        this.requiredModIds = requiredModIds;
        this.legacy = legacy;
        this.common = common;
        this.client = client;
    }

    private static boolean isDuplicate(ResourceLocation rl) {
        if (PLUGIN_ID_TO_PLUGIN_INFO.containsKey(rl)) {
            LOG.error("Duplicate plugin id " + rl);
            return true;
        }
        return false;
    }

    public static void register(
        String modId,
        String pluginIdStr,
        PluginSide side,
        @Nullable String commonCls,
        @Nullable String clientCls,
        List<String> required,
        boolean defaultEnabled
    ) {
        var rl = ResourceLocation.parse(pluginIdStr);
        if (isDuplicate(rl)) return;

        if (rl.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) {
            LOG.warn("Plugin " + commonCls + " is using the default namespace " + rl);
        }

        var common = commonCls == null ? null : Suppliers.memoize(() -> {
            try {
                return (IWailaCommonPlugin) Class.forName(commonCls).getConstructor().newInstance();
            } catch (Throwable t) {
                LOG.error("Error creating instance of plugin " + pluginIdStr, t);
            }
            return null;
        });

        var client = clientCls == null ? null : Suppliers.memoize(() -> {
            try {
                return (IWailaClientPlugin) Class.forName(clientCls).getConstructor().newInstance();
            } catch (Throwable t) {
                LOG.error("Error creating instance of plugin " + pluginIdStr, t);
            }
            return null;
        });

        PLUGIN_ID_TO_PLUGIN_INFO.put(rl, new PluginInfo(ModInfo.get(modId), rl, side, null, required, false, common, client));
        TOGGLE.get().put(rl, defaultEnabled);
    }

    public static void registerDeprecated(
        String modId,
        String pluginIdStr,
        PluginSide side,
        String initializerStr,
        List<String> required,
        boolean defaultEnabled,
        boolean legacy
    ) {
        try {
            var rl = ResourceLocation.parse(pluginIdStr);
            if (isDuplicate(rl)) return;

            if (rl.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) {
                LOG.warn("Plugin " + initializerStr + " is using the default namespace " + rl);
            }

            var initializer = (IWailaPlugin) Class.forName(initializerStr).getConstructor().newInstance();
            PLUGIN_ID_TO_PLUGIN_INFO.put(rl, new PluginInfo(ModInfo.get(modId), rl, side, initializer, required, legacy, null, null));
            TOGGLE.get().putIfAbsent(rl, defaultEnabled);
        } catch (Throwable t) {
            LOG.error("Error creating instance of plugin " + pluginIdStr, t);
        }
    }

    public static PluginInfo get(ResourceLocation pluginId) {
        return PLUGIN_ID_TO_PLUGIN_INFO.get(pluginId);
    }

    public static Collection<PluginInfo> getAllFromMod(String modId) {
        return MOD_ID_TO_PLUGIN_INFOS.get().get(modId);
    }

    public static Collection<PluginInfo> getAll() {
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
        return side.toDeprecated();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IWailaPlugin getInitializer() {
        if (deprecatedInit == null) return EMPTY_INIT;
        return deprecatedInit;
    }

    @SuppressWarnings("deprecation")
    public @Nullable IWailaPlugin getDeprecatedInit() {
        return deprecatedInit;
    }

    public @Nullable IWailaCommonPlugin getCommon() {
        if (common == null) return null;
        return common.get();
    }

    public @Nullable IWailaClientPlugin getClient() {
        if (client == null) return null;
        if (!Waila.CLIENT_SIDE) return null;
        return client.get();
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
        getAll().forEach(it -> it.disabledOnServer = false);
    }

    public static void saveToggleConfig() {
        TOGGLE.save();
    }

}
