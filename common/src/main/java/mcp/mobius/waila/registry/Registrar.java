package mcp.mobius.waila.registry;

import java.util.EnumMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public enum Registrar implements IRegistrar {

    INSTANCE;

    public final Registry<IBlockComponentProvider> blockOverride = new Registry<>();
    public final Registry<IBlockComponentProvider> blockItem = new Registry<>();
    public final Registry<IServerDataProvider<BlockEntity>> blockData = new Registry<>();
    public final Map<TooltipPosition, Registry<IBlockComponentProvider>> blockComponent = Util.make(new EnumMap<>(TooltipPosition.class), map -> {
        for (TooltipPosition key : TooltipPosition.values()) {
            map.put(key, new Registry<>());
        }
    });

    public final Registry<IEntityComponentProvider> entityOverride = new Registry<>();
    public final Registry<IEntityComponentProvider> entityItem = new Registry<>();
    public final Registry<IServerDataProvider<Entity>> entityData = new Registry<>();
    public final Map<TooltipPosition, Registry<IEntityComponentProvider>> entityComponent = Util.make(new EnumMap<>(TooltipPosition.class), map -> {
        for (TooltipPosition key : TooltipPosition.values()) {
            map.put(key, new Registry<>());
        }
    });

    public final Map<ResourceLocation, ITooltipRenderer> renderer = new Object2ObjectOpenHashMap<>();

    private boolean locked = false;

    private <T> void addConfig(ResourceLocation key, T defaultValue, boolean synced, ConfigEntry.Type<T> type) {
        if (!synced && !Waila.clientSide) {
            return;
        }

        assertLock();
        PluginConfig.INSTANCE.addConfig(type.create(key, defaultValue, synced));
    }

    @Override
    public void addConfig(ResourceLocation key, boolean defaultValue) {
        addConfig(key, defaultValue, false, ConfigEntry.BOOLEAN);
    }

    @Override
    public void addConfig(ResourceLocation key, int defaultValue) {
        addConfig(key, defaultValue, false, ConfigEntry.INTEGER);
    }

    @Override
    public void addConfig(ResourceLocation key, double defaultValue) {
        addConfig(key, defaultValue, false, ConfigEntry.DOUBLE);
    }

    @Override
    public void addConfig(ResourceLocation key, String defaultValue) {
        addConfig(key, defaultValue, false, ConfigEntry.STRING);
    }

    @Override
    public <T extends Enum<T>> void addConfig(ResourceLocation key, T defaultValue) {
        addConfig(key, defaultValue, false, ConfigEntry.ENUM);
    }

    @Override
    public void addSyncedConfig(ResourceLocation key, boolean defaultValue) {
        addConfig(key, defaultValue, true, ConfigEntry.BOOLEAN);
    }

    @Override
    public void addSyncedConfig(ResourceLocation key, int defaultValue) {
        addConfig(key, defaultValue, true, ConfigEntry.INTEGER);
    }

    @Override
    public void addSyncedConfig(ResourceLocation key, double defaultValue) {
        addConfig(key, defaultValue, true, ConfigEntry.DOUBLE);
    }

    @Override
    public void addSyncedConfig(ResourceLocation key, String defaultValue) {
        addConfig(key, defaultValue, true, ConfigEntry.STRING);
    }

    @Override
    public <T extends Enum<T>> void addSyncedConfig(ResourceLocation key, T defaultValue) {
        addConfig(key, defaultValue, false, ConfigEntry.ENUM);
    }

    @Override
    public <T> void addOverride(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        if (Waila.clientSide) {
            assertLock();
            assertPriority(priority);
            blockOverride.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addDisplayItem(IBlockComponentProvider provider, Class<T> clazz, int priority) {
        if (Waila.clientSide) {
            assertLock();
            assertPriority(priority);
            blockItem.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority) {
        if (Waila.clientSide) {
            assertLock();
            assertPriority(priority);
            blockComponent.get(position).add(clazz, provider, priority);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, BE extends BlockEntity> void addBlockData(IServerDataProvider<BE> provider, Class<T> clazz) {
        assertLock();
        blockData.add(clazz, (IServerDataProvider<BlockEntity>) provider, 0);
    }

    @Override
    public <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        if (Waila.clientSide) {
            assertLock();
            assertPriority(priority);
            entityOverride.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addDisplayItem(IEntityComponentProvider provider, Class<T> clazz, int priority) {
        if (Waila.clientSide) {
            assertLock();
            assertPriority(priority);
            entityItem.add(clazz, provider, priority);
        }
    }

    @Override
    public <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority) {
        if (Waila.clientSide) {
            assertLock();
            assertPriority(priority);
            entityComponent.get(position).add(clazz, provider, priority);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, E extends Entity> void addEntityData(IServerDataProvider<E> provider, Class<T> clazz) {
        assertLock();
        entityData.add(clazz, (IServerDataProvider<Entity>) provider, 0);
    }

    @Override
    public void addRenderer(ResourceLocation id, ITooltipRenderer renderer) {
        if (Waila.clientSide) {
            this.renderer.put(id, renderer);
        }
    }

    public void lock() {
        locked = true;
    }

    private void assertLock() {
        Preconditions.checkState(!locked,
            "Tried to register new component after the registrar is locked");
    }

    private void assertPriority(int priority) {
        Preconditions.checkArgument(priority >= 0,
            "Priority must be equals or more than 0");
    }

}
