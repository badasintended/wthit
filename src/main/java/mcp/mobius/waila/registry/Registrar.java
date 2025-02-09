package mcp.mobius.waila.registry;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.IRayCastVectorProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IThemeType;
import mcp.mobius.waila.api.IToolType;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.api.__internal__.IHarvestService;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.hud.TooltipPosition;
import mcp.mobius.waila.gui.hud.theme.ThemeType;
import mcp.mobius.waila.util.CachedSupplier;
import mcp.mobius.waila.util.Log;
import mcp.mobius.waila.util.TypeUtil;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class Registrar implements ICommonRegistrar, IClientRegistrar, IRegistrar {

    private static final CachedSupplier<Registrar> INSTANCE = new CachedSupplier<>(Registrar::new);

    private static final Log LOG = Log.create();

    public final InstanceRegistry<IBlockComponentProvider> blockRedirect = new InstanceRegistry<>();
    public final InstanceRegistry<IBlockComponentProvider> blockOverride = new InstanceRegistry<>();
    public final InstanceRegistry<PluginAware<IBlockComponentProvider>> blockIcon = new InstanceRegistry<>();
    public final InstanceRegistry<IBlockComponentProvider> blockDataCtx = new InstanceRegistry<>();
    public final InstanceRegistry<IDataProvider<BlockEntity>> blockData = new InstanceRegistry<>();
    public final Map<TooltipPosition, InstanceRegistry<PluginAware<IBlockComponentProvider>>> blockComponent = Util.make(new EnumMap<>(TooltipPosition.class), map -> {
        for (var key : TooltipPosition.values()) {
            map.put(key, new InstanceRegistry<>());
        }
    });

    public final InstanceRegistry<IEntityComponentProvider> entityRedirect = new InstanceRegistry<>();
    public final InstanceRegistry<IEntityComponentProvider> entityOverride = new InstanceRegistry<>();
    public final InstanceRegistry<PluginAware<IEntityComponentProvider>> entityIcon = new InstanceRegistry<>();
    public final InstanceRegistry<IEntityComponentProvider> entityDataCtx = new InstanceRegistry<>();
    public final InstanceRegistry<IDataProvider<Entity>> entityData = new InstanceRegistry<>();
    public final Map<TooltipPosition, InstanceRegistry<PluginAware<IEntityComponentProvider>>> entityComponent = Util.make(new EnumMap<>(TooltipPosition.class), map -> {
        for (var key : TooltipPosition.values()) {
            map.put(key, new InstanceRegistry<>());
        }
    });

    public final InstanceRegistry<PluginAware<IEventListener>> eventListeners = Util.make(new InstanceRegistry<>(), InstanceRegistry::reversed);
    public final InstanceRegistry<IRayCastVectorProvider> raycastVectorProviders = new InstanceRegistry<>();

    public final BlacklistConfig blacklist = new BlacklistConfig();
    public final InstanceRegistry<Consumer<BlacklistConfig>> blacklistModifiers = Util.make(new InstanceRegistry<>(), InstanceRegistry::reversed);

    public final Map<ResourceLocation, IntFormat> intConfigFormats = new HashMap<>();

    public final BiMap<ResourceLocation, ThemeType<?>> themeTypes = HashBiMap.create();

    public final Map<ResourceLocation, StreamCodec<RegistryFriendlyByteBuf, IData>> dataCodecs = new HashMap<>();

    private @Nullable IPluginInfo plugin;
    private boolean locked = false;

    public static Registrar get() {
        return INSTANCE.get();
    }

    public static void destroy() {
        INSTANCE.invalidate();
    }

    public void attach(@Nullable IPluginInfo plugin) {
        this.plugin = plugin;
    }

    private <T> void addConfig(ResourceLocation key, T defaultValue, T clientOnlyValue, boolean serverRequired, boolean merged, ConfigEntry.Type<T> type) {
        assertLock();
        PluginConfig.addConfig(type.create(plugin, key, defaultValue, clientOnlyValue, serverRequired, merged));
    }

    @SafeVarargs
    private <T> void modifyBlacklist(int priority, Function<BlacklistConfig, Set<String>> getter, BiConsumer<Set<String>, String> modifier, Registry<T> registry, T... values) {
        assertLock();
        if (skip()) return;

        blacklistModifiers.add(Object.class, blacklist -> {
            for (var value : values) {
                modifier.accept(getter.apply(blacklist), Objects.requireNonNull(registry.getKey(value)).toString());
            }
        }, priority);
    }

    @Override
    public void localConfig(ResourceLocation key, boolean defaultValue) {
        addConfig(key, defaultValue, defaultValue, false, false, ConfigEntry.BOOLEAN);
    }

    @Override
    public void localConfig(ResourceLocation key, int defaultValue, IntFormat format) {
        intConfigFormats.put(key, format);
        addConfig(key, defaultValue, defaultValue, false, false, ConfigEntry.INTEGER);
    }

    @Override
    public void localConfig(ResourceLocation key, double defaultValue) {
        addConfig(key, defaultValue, defaultValue, false, false, ConfigEntry.DOUBLE);
    }

    @Override
    public void localConfig(ResourceLocation key, String defaultValue) {
        addConfig(key, defaultValue, defaultValue, false, false, ConfigEntry.STRING);
    }

    @Override
    public <T extends Enum<T>> void localConfig(ResourceLocation key, T defaultValue) {
        addConfig(key, defaultValue, defaultValue, false, false, ConfigEntry.ENUM);
    }

    @Override
    public void externalConfig(ResourceLocation key, Path path) {
        addConfig(key, path, path, false, false, ConfigEntry.PATH);
    }

    @Override
    public void featureConfig(ResourceLocation key, boolean clientOnly) {
        addConfig(key, true, clientOnly, !clientOnly, true, ConfigEntry.BOOLEAN);
    }

    @Override
    public void syncedConfig(ResourceLocation key, boolean defaultValue, boolean clientOnlyValue) {
        addConfig(key, defaultValue, clientOnlyValue, true, false, ConfigEntry.BOOLEAN);
    }

    @Override
    public void syncedConfig(ResourceLocation key, int defaultValue, int clientOnlyValue, IntFormat format) {
        intConfigFormats.put(key, format);
        addConfig(key, defaultValue, clientOnlyValue, true, false, ConfigEntry.INTEGER);
    }

    @Override
    public void syncedConfig(ResourceLocation key, double defaultValue, double clientOnlyValue) {
        addConfig(key, defaultValue, clientOnlyValue, true, false, ConfigEntry.DOUBLE);
    }

    @Override
    public void syncedConfig(ResourceLocation key, String defaultValue, String clientOnlyValue) {
        addConfig(key, defaultValue, clientOnlyValue, true, false, ConfigEntry.STRING);
    }

    @Override
    public <T extends Enum<T>> void syncedConfig(ResourceLocation key, T defaultValue, T clientOnlyValue) {
        addConfig(key, defaultValue, clientOnlyValue, true, false, ConfigEntry.ENUM);
    }

    @Override
    public void configAlias(ResourceLocation actual, ResourceLocation... aliases) {
        assertLock();

        for (var alias : aliases) {
            PluginConfig.addConfig(PluginConfig.getEntry(actual).createAlias(alias));
        }
    }

    @Override
    public void eventListener(IEventListener listener, int priority) {
        if (skip()) return;
        assertLock();
        eventListeners.add(Object.class, new PluginAware<>(plugin, listener), priority);
    }

    @Override
    public void blacklist(int priority, Block... blocks) {
        modifyBlacklist(priority, it -> it.blocks, Set::add, BuiltInRegistries.BLOCK, blocks);
    }

    @Override
    public void blacklist(int priority, BlockEntityType<?>... blockEntityTypes) {
        modifyBlacklist(priority, it -> it.blockEntityTypes, Set::add, BuiltInRegistries.BLOCK_ENTITY_TYPE, blockEntityTypes);
    }

    @Override
    public void removeBlacklist(int priority, Block... blocks) {
        modifyBlacklist(priority, it -> it.blocks, Set::remove, BuiltInRegistries.BLOCK, blocks);
    }

    @Override
    public void removeBlacklist(int priority, BlockEntityType<?>... blockEntityTypes) {
        modifyBlacklist(priority, it -> it.blockEntityTypes, Set::remove, BuiltInRegistries.BLOCK_ENTITY_TYPE, blockEntityTypes);
    }

    @Override
    public <T> void redirect(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        if (skip()) return;
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            blockRedirect.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void override(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        if (skip()) return;
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            blockOverride.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void icon(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        if (skip()) return;
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            warnTargetClass(provider, clazz);
            blockIcon.add(clazz, new PluginAware<>(plugin, provider), priority);
        }
    }

    private <T> void component(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority) {
        if (skip()) return;
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            warnTargetClass(provider, clazz);
            blockComponent.get(position).add(clazz, new PluginAware<>(plugin, provider), priority);
        }
    }

    @Override
    public <T> void head(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        component(provider, TooltipPosition.HEAD, clazz, priority);
    }

    @Override
    public <T> void body(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        component(provider, TooltipPosition.BODY, clazz, priority);
    }

    @Override
    public <T> void tail(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        component(provider, TooltipPosition.TAIL, clazz, priority);
    }

    @Override
    public <T> void dataContext(IBlockComponentProvider provider, Class<T> clazz) {
        if (skip()) return;
        if (Waila.CLIENT_SIDE) {
            assertLock();
            warnTargetClass(provider, clazz);
            blockDataCtx.add(clazz, provider, 0);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, BE extends BlockEntity> void blockData(IDataProvider<BE> provider, Class<T> clazz, int priority) {
        if (skip()) return;
        assertLock();
        assertPriority(priority);
        warnTargetClass(provider, clazz);
        blockData.add(clazz, (IDataProvider<BlockEntity>) provider, priority);
    }

    @Override
    public void blacklist(int priority, EntityType<?>... entityTypes) {
        modifyBlacklist(priority, it -> it.entityTypes, Set::add, BuiltInRegistries.ENTITY_TYPE, entityTypes);
    }

    @Override
    public void removeBlacklist(int priority, EntityType<?>... entityTypes) {
        modifyBlacklist(priority, it -> it.entityTypes, Set::remove, BuiltInRegistries.ENTITY_TYPE, entityTypes);
    }

    @Override
    public <T> void redirect(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        if (skip()) return;
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            entityRedirect.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void override(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        if (skip()) return;
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            warnTargetClass(provider, clazz);
            entityOverride.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void icon(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        if (skip()) return;
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            warnTargetClass(provider, clazz);
            entityIcon.add(clazz, new PluginAware<>(plugin, provider), priority);
        }
    }

    private <T> void component(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority) {
        if (skip()) return;
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            warnTargetClass(provider, clazz);
            entityComponent.get(position).add(clazz, new PluginAware<>(plugin, provider), priority);
        }
    }

    @Override
    public <T> void head(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        component(provider, TooltipPosition.HEAD, clazz, priority);
    }

    @Override
    public <T> void body(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        component(provider, TooltipPosition.BODY, clazz, priority);
    }

    @Override
    public <T> void tail(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        component(provider, TooltipPosition.TAIL, clazz, priority);
    }

    @Override
    public <T> void dataContext(IEntityComponentProvider provider, Class<T> clazz) {
        if (skip()) return;
        if (Waila.CLIENT_SIDE) {
            assertLock();
            warnTargetClass(provider, clazz);
            entityDataCtx.add(clazz, provider, 0);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, E extends Entity> void entityData(IDataProvider<E> provider, Class<T> clazz, int priority) {
        if (skip()) return;
        assertLock();
        assertPriority(priority);
        warnTargetClass(provider, clazz);
        entityData.add(clazz, (IDataProvider<Entity>) provider, priority);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <D extends IData> void dataType(IData.Type<D> type, StreamCodec<? super RegistryFriendlyByteBuf, ? extends D> codec) {
        assertLock();
        Preconditions.checkArgument(!dataCodecs.containsKey(type.id()), "Data type with id %s already present", type.id());
        dataCodecs.put(type.id(), (StreamCodec<RegistryFriendlyByteBuf, IData>) codec);
    }

    @Override
    public <T extends ITheme> void themeType(ResourceLocation id, IThemeType<T> type) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            ThemeType<T> casted = TypeUtil.uncheckedCast(type);
            themeTypes.put(id, casted);
        }
    }

    @Override
    public void rayCastVector(IRayCastVectorProvider provider, int priority) {
        if (skip()) return;
        if (Waila.CLIENT_SIDE) {
            assertLock();
            raycastVectorProviders.add(Object.class, provider, priority);
        }
    }

    @Override
    public void toolType(ResourceLocation id, IToolType toolType) {
        IHarvestService.INSTANCE.addToolType(id, toolType);
    }

    public void lock() {
        locked = true;

        if (Waila.CLIENT_SIDE) {
            Preconditions.checkState(!raycastVectorProviders.get(Object.class).isEmpty(), "No raycast vector provider found");
        }

        blacklistModifiers.get(Object.class).forEach(it -> it.instance().accept(blacklist));
        blacklist.addBlacklistTags();

        var hash = new int[]{
            blacklist.blocks.hashCode(),
            blacklist.blockEntityTypes.hashCode(),
            blacklist.entityTypes.hashCode()};

        Waila.BLACKLIST_CONFIG.invalidate();
        var userBlacklist = Waila.BLACKLIST_CONFIG.get();

        if (!Arrays.equals(userBlacklist.pluginHash, hash)) {
            if (!Arrays.equals(userBlacklist.pluginHash, new int[]{0, 0, 0})) {
                var userHash = new int[]{
                    userBlacklist.blocks.hashCode(),
                    userBlacklist.blockEntityTypes.hashCode(),
                    userBlacklist.entityTypes.hashCode()};

                if (!Arrays.equals(userBlacklist.pluginHash, userHash)) {
                    Waila.BLACKLIST_CONFIG.backup("plugin hash mismatch");
                }
            }

            var newBlacklist = Waila.BLACKLIST_CONFIG.get();
            newBlacklist.pluginHash = hash;

            newBlacklist.blocks.clear();
            newBlacklist.blocks.addAll(blacklist.blocks);

            newBlacklist.blockEntityTypes.clear();
            newBlacklist.blockEntityTypes.addAll(blacklist.blockEntityTypes);

            newBlacklist.entityTypes.clear();
            newBlacklist.entityTypes.addAll(blacklist.entityTypes);
        }

        Waila.BLACKLIST_CONFIG.save();

    }

    private void assertPlugin() {
        Preconditions.checkNotNull(plugin, "Tried to register things outside the register method");
    }

    @SuppressWarnings("DataFlowIssue")
    private boolean skip() {
        assertPlugin();
        return !plugin.isEnabled();
    }

    private void assertLock() {
        assertPlugin();
        Preconditions.checkState(!locked, "Tried to register new component after the registrar is locked");
    }

    private void assertPriority(int priority) {
        Preconditions.checkArgument(priority >= 0, "Priority must be equals or more than 0");
    }

    private void warnTargetClass(Object object, Class<?> clazz) {
        if (Waila.DEV && clazz.isInstance(object)) {
            LOG.warn("The target class {} is the same as the provider class, this is probably an error", clazz.getSimpleName());
        }
    }

}
