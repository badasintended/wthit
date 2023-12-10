package mcp.mobius.waila.registry;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.api.IRayCastVectorProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IThemeType;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.hud.theme.ThemeType;
import mcp.mobius.waila.util.Log;
import mcp.mobius.waila.util.TypeUtil;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public enum Registrar implements IRegistrar {

    INSTANCE;

    private static final Log LOG = Log.create();

    public final Register<IBlockComponentProvider> blockOverride = new Register<>();
    public final Register<IBlockComponentProvider> blockIcon = new Register<>();
    public final Register<IBlockComponentProvider> blockDataCtx = new Register<>();
    public final Register<IDataProvider<BlockEntity>> blockData = new Register<>();
    public final Map<TooltipPosition, Register<IBlockComponentProvider>> blockComponent = Util.make(new EnumMap<>(TooltipPosition.class), map -> {
        for (var key : TooltipPosition.values()) {
            map.put(key, new Register<>());
        }
    });

    public final Register<IEntityComponentProvider> entityOverride = new Register<>();
    public final Register<IEntityComponentProvider> entityIcon = new Register<>();
    public final Register<IEntityComponentProvider> entityDataCtx = new Register<>();
    public final Register<IDataProvider<Entity>> entityData = new Register<>();
    public final Map<TooltipPosition, Register<IEntityComponentProvider>> entityComponent = Util.make(new EnumMap<>(TooltipPosition.class), map -> {
        for (var key : TooltipPosition.values()) {
            map.put(key, new Register<>());
        }
    });

    public final Register<IEventListener> eventListeners = Util.make(new Register<>(), Register::reversed);
    public final Register<IRayCastVectorProvider> raycastVectorProviders = new Register<>();

    public final BlacklistConfig blacklist = new BlacklistConfig();

    public final Map<ResourceLocation, IntFormat> intConfigFormats = new HashMap<>();

    public final BiMap<ResourceLocation, ThemeType<?>> themeTypes = HashBiMap.create();

    public final Map<Class<? extends IData>, ResourceLocation> dataType2Id = new HashMap<>();
    public final Map<ResourceLocation, IData.Serializer<?>> dataId2Serializer = new HashMap<>();

    @Nullable
    public IObjectPicker picker = null;
    private int pickerPriority = Integer.MAX_VALUE;

    private boolean locked = false;

    private <T> void addConfig(ResourceLocation key, T defaultValue, T clientOnlyValue, boolean serverRequired, boolean merged, ConfigEntry.Type<T> type) {
        assertLock();
        PluginConfig.addConfig(type.create(key, defaultValue, clientOnlyValue, serverRequired, merged));
    }

    @SafeVarargs
    private <T> void addBlacklist(Set<String> set, Registry<T> registry, T... values) {
        assertLock();

        for (var value : values) {
            set.add(Objects.requireNonNull(registry.getKey(value)).toString());
        }
    }

    @Override
    public void addConfig(ResourceLocation key, boolean defaultValue) {
        addConfig(key, defaultValue, defaultValue, false, false, ConfigEntry.BOOLEAN);
    }

    @Override
    public void addConfig(ResourceLocation key, int defaultValue, IntFormat format) {
        intConfigFormats.put(key, format);
        addConfig(key, defaultValue, defaultValue, false, false, ConfigEntry.INTEGER);
    }

    @Override
    public void addConfig(ResourceLocation key, double defaultValue) {
        addConfig(key, defaultValue, defaultValue, false, false, ConfigEntry.DOUBLE);
    }

    @Override
    public void addConfig(ResourceLocation key, String defaultValue) {
        addConfig(key, defaultValue, defaultValue, false, false, ConfigEntry.STRING);
    }

    @Override
    public <T extends Enum<T>> void addConfig(ResourceLocation key, T defaultValue) {
        addConfig(key, defaultValue, defaultValue, false, false, ConfigEntry.ENUM);
    }

    @Override
    public void addConfig(ResourceLocation key, Path path) {
        addConfig(key, path, path, false, false, ConfigEntry.PATH);
    }

    @Override
    public void addFeatureConfig(ResourceLocation key, boolean clientOnly) {
        addConfig(key, true, clientOnly, !clientOnly, true, ConfigEntry.BOOLEAN);
    }

    @Override
    public void addSyncedConfig(ResourceLocation key, boolean defaultValue, boolean clientOnlyValue) {
        addConfig(key, defaultValue, clientOnlyValue, true, false, ConfigEntry.BOOLEAN);
    }

    @Override
    public void addSyncedConfig(ResourceLocation key, int defaultValue, int clientOnlyValue, IntFormat format) {
        intConfigFormats.put(key, format);
        addConfig(key, defaultValue, clientOnlyValue, true, false, ConfigEntry.INTEGER);
    }

    @Override
    public void addSyncedConfig(ResourceLocation key, double defaultValue, double clientOnlyValue) {
        addConfig(key, defaultValue, clientOnlyValue, true, false, ConfigEntry.DOUBLE);
    }

    @Override
    public void addSyncedConfig(ResourceLocation key, String defaultValue, String clientOnlyValue) {
        addConfig(key, defaultValue, clientOnlyValue, true, false, ConfigEntry.STRING);
    }

    @Override
    public <T extends Enum<T>> void addSyncedConfig(ResourceLocation key, T defaultValue, T clientOnlyValue) {
        addConfig(key, defaultValue, clientOnlyValue, true, false, ConfigEntry.ENUM);
    }

    @Override
    public void addEventListener(IEventListener listener, int priority) {
        assertLock();
        eventListeners.add(Object.class, listener, priority);
    }

    @Override
    public void addBlacklist(Block... blocks) {
        addBlacklist(blacklist.blocks, Registry.BLOCK, blocks);
    }

    @Override
    public void addBlacklist(BlockEntityType<?>... blockEntityTypes) {
        addBlacklist(blacklist.blockEntityTypes, Registry.BLOCK_ENTITY_TYPE, blockEntityTypes);
    }

    @Override
    public <T> void addOverride(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            blockOverride.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addIcon(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            warnTargetClass(provider, clazz);
            blockIcon.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            warnTargetClass(provider, clazz);
            blockComponent.get(position).add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addDataContext(IBlockComponentProvider provider, Class<T> clazz) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            warnTargetClass(provider, clazz);
            blockDataCtx.add(clazz, provider, 0);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, BE extends BlockEntity> void addBlockData(IDataProvider<BE> provider, Class<T> clazz, int priority) {
        assertLock();
        assertPriority(priority);
        warnTargetClass(provider, clazz);
        blockData.add(clazz, (IDataProvider<BlockEntity>) provider, priority);
    }

    @Override
    public void addBlacklist(EntityType<?>... entityTypes) {
        addBlacklist(blacklist.entityTypes, Registry.ENTITY_TYPE, entityTypes);
    }

    @Override
    public <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            warnTargetClass(provider, clazz);
            entityOverride.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addIcon(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            warnTargetClass(provider, clazz);
            entityIcon.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            warnTargetClass(provider, clazz);
            entityComponent.get(position).add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addDataContext(IEntityComponentProvider provider, Class<T> clazz) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            warnTargetClass(provider, clazz);
            entityDataCtx.add(clazz, provider, 0);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, E extends Entity> void addEntityData(IDataProvider<E> provider, Class<T> clazz, int priority) {
        assertLock();
        assertPriority(priority);
        warnTargetClass(provider, clazz);
        entityData.add(clazz, (IDataProvider<Entity>) provider, priority);
    }

    @Override
    public <T extends IData> void addDataType(ResourceLocation id, Class<T> type, IData.Serializer<T> serializer) {
        assertLock();
        Preconditions.checkArgument(!dataId2Serializer.containsKey(id), "Data type with id %s already present", id);

        dataType2Id.put(type, id);
        dataId2Serializer.put(id, serializer);
    }

    @Override
    public <T extends ITheme> void addThemeType(ResourceLocation id, IThemeType<T> type) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            ThemeType<T> casted = TypeUtil.uncheckedCast(type);
            themeTypes.put(id, casted);
        }
    }

    @Override
    public void addRayCastVector(IRayCastVectorProvider provider, int priority) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            raycastVectorProviders.add(Object.class, provider, priority);
        }
    }

    @Override
    public void replacePicker(IObjectPicker picker, int priority) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            if (priority <= pickerPriority) {
                this.picker = picker;
                this.pickerPriority = priority;
            }
        }
    }

    public void lock() {
        locked = true;

        if (Waila.CLIENT_SIDE) {
            if (picker != null) {
                LOG.info("Using {} as the object picker", picker.getClass().getName());
            } else {
                Preconditions.checkState(!raycastVectorProviders.get(Object.class).isEmpty(), "No raycast vector provider found");
            }
        }

        var hash = new int[]{0, 0, 0};
        hash[0] = blacklist.blocks.hashCode();
        hash[1] = blacklist.blockEntityTypes.hashCode();
        hash[2] = blacklist.entityTypes.hashCode();

        var userBlacklist = Waila.BLACKLIST_CONFIG.get();

        if (!Arrays.equals(userBlacklist.pluginHash, hash)) {
            if (!Arrays.equals(userBlacklist.pluginHash, new int[]{0, 0, 0})) {
                Waila.BLACKLIST_CONFIG.backup("plugin hash mismatch");
            }

            var newBlacklist = Waila.BLACKLIST_CONFIG.get();
            newBlacklist.pluginHash = hash;
            newBlacklist.blocks.addAll(blacklist.blocks);
            newBlacklist.blockEntityTypes.addAll(blacklist.blockEntityTypes);
            newBlacklist.entityTypes.addAll(blacklist.entityTypes);
        }

        Waila.BLACKLIST_CONFIG.save();
    }

    private void assertLock() {
        Preconditions.checkState(!locked,
            "Tried to register new component after the registrar is locked");
    }

    private void assertPriority(int priority) {
        Preconditions.checkArgument(priority >= 0,
            "Priority must be equals or more than 0");
    }

    private void warnTargetClass(Object object, Class<?> clazz) {
        if (Waila.DEV && clazz.isInstance(object)) {
            LOG.warn("The target class {} is the same as the provider class, this is probably an error", clazz.getSimpleName());
        }
    }

}
