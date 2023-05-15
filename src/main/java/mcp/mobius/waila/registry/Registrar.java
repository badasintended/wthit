package mcp.mobius.waila.registry;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IThemeType;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.hud.theme.ThemeType;
import mcp.mobius.waila.util.TypeUtil;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public enum Registrar implements IRegistrar {

    INSTANCE;

    public final Register<IBlockComponentProvider> blockOverride = new Register<>();
    public final Register<IBlockComponentProvider> blockIcon = new Register<>();
    public final Register<IDataProvider<BlockEntity>> blockData = new Register<>();
    public final Map<TooltipPosition, Register<IBlockComponentProvider>> blockComponent = Util.make(new EnumMap<>(TooltipPosition.class), map -> {
        for (TooltipPosition key : TooltipPosition.values()) {
            map.put(key, new Register<>());
        }
    });

    public final Register<IEntityComponentProvider> entityOverride = new Register<>();
    public final Register<IEntityComponentProvider> entityIcon = new Register<>();
    public final Register<IDataProvider<Entity>> entityData = new Register<>();
    public final Map<TooltipPosition, Register<IEntityComponentProvider>> entityComponent = Util.make(new EnumMap<>(TooltipPosition.class), map -> {
        for (TooltipPosition key : TooltipPosition.values()) {
            map.put(key, new Register<>());
        }
    });

    public final Register<IEventListener> eventListeners = Util.make(new Register<>(), Register::reversed);
    public final BlacklistConfig blacklist = new BlacklistConfig();

    public final Map<ResourceLocation, IntFormat> intConfigFormats = new HashMap<>();

    public final BiMap<ResourceLocation, ThemeType<?>> themeTypes = HashBiMap.create();
    private final Map<Class<? extends ITheme>, ThemeType<?>> themeClass2Type = new HashMap<>();

    public final Map<Class<? extends IData>, ResourceLocation> dataType2Id = new HashMap<>();
    public final Map<ResourceLocation, IData.Serializer<?>> dataId2Serializer = new HashMap<>();

    private int pickerPriority = Integer.MAX_VALUE;
    public IObjectPicker picker = null;

    private boolean locked = false;

    private <T> void addConfig(ResourceLocation key, T defaultValue, T clientOnlyValue, boolean serverRequired, boolean merged, ConfigEntry.Type<T> type) {
        assertLock();
        PluginConfig.addConfig(type.create(key, defaultValue, clientOnlyValue, serverRequired, merged));
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
    public void addMergedConfig(ResourceLocation key, boolean defaultValue) {
        addConfig(key, defaultValue, defaultValue, false, true, ConfigEntry.BOOLEAN);
    }

    @Override
    public void addMergedSyncedConfig(ResourceLocation key, boolean defaultValue, boolean clientOnlyValue) {
        addConfig(key, defaultValue, clientOnlyValue, true, true, ConfigEntry.BOOLEAN);
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
        assertLock();
        blacklist.blocks.addAll(Arrays.asList(blocks));
    }

    @Override
    public void addBlacklist(BlockEntityType<?>... blockEntityTypes) {
        blacklist.blockEntityTypes.addAll(Arrays.asList(blockEntityTypes));
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
            blockIcon.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            blockComponent.get(position).add(clazz, provider, priority);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, BE extends BlockEntity> void addBlockData(IDataProvider<BE> provider, Class<T> clazz, int priority) {
        assertLock();
        assertPriority(priority);
        blockData.add(clazz, (IDataProvider<BlockEntity>) provider, priority);
    }

    @Override
    public void addBlacklist(EntityType<?>... entityTypes) {
        assertLock();
        blacklist.entityTypes.addAll(Arrays.asList(entityTypes));
    }

    @Override
    public <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            entityOverride.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addIcon(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            entityIcon.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority) {
        if (Waila.CLIENT_SIDE) {
            assertLock();
            assertPriority(priority);
            entityComponent.get(position).add(clazz, provider, priority);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, E extends Entity> void addEntityData(IDataProvider<E> provider, Class<T> clazz, int priority) {
        assertLock();
        assertPriority(priority);
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
            themeClass2Type.put(casted.clazz, casted);
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
            Preconditions.checkState(picker != null, "No object picker registered");
            Waila.LOGGER.info("Using {} as the object picker", picker.getClass().getName());
        }

        int[] hash = {0, 0, 0};
        hash[0] = hash(blacklist.blocks, BuiltInRegistries.BLOCK);
        hash[1] = hash(blacklist.blockEntityTypes, BuiltInRegistries.BLOCK_ENTITY_TYPE);
        hash[2] = hash(blacklist.entityTypes, BuiltInRegistries.ENTITY_TYPE);

        if (Waila.BLACKLIST_CONFIG.isFileExists() && !Arrays.equals(Waila.BLACKLIST_CONFIG.get().pluginHash, hash)) {
            Waila.BLACKLIST_CONFIG.backup();
        }

        BlacklistConfig newBlacklist = Waila.BLACKLIST_CONFIG.get();
        newBlacklist.pluginHash = hash;
        newBlacklist.blocks.addAll(blacklist.blocks);
        newBlacklist.entityTypes.addAll(blacklist.entityTypes);
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

    private <T> int hash(Set<T> set, Registry<T> registry) {
        return set.stream().map(registry::getKey).collect(Collectors.toSet()).hashCode();
    }

}
