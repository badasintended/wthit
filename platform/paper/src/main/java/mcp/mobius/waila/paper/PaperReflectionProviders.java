package mcp.mobius.waila.paper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import mcp.mobius.waila.api.data.ProgressData;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.BeaconDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.BeehiveDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.LecternDataProvider;
import mcp.mobius.waila.util.Log;
import net.minecraft.core.Holder;
import net.minecraft.world.LockCode;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jspecify.annotations.Nullable;

/**
 * Reflection-based replacements for the mixin data providers.
 * <p>
 * Paper uses Mojang-mapped NMS so we can access private fields via reflection
 * instead of Fabric's mixin accessors.
 */
final class PaperReflectionProviders {

    private static final Log LOG = Log.create();

    private PaperReflectionProviders() {
    }

    private static @Nullable Field field(Class<?> clazz, String name) {
        try {
            var f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            LOG.warn("Failed to resolve field " + clazz.getSimpleName() + "." + name + ", provider will be a no-op: " + e.getMessage());
            return null;
        }
    }

    enum Furnace implements IDataProvider<AbstractFurnaceBlockEntity> {
        INSTANCE;

        private static final @Nullable Field COOKING_TIMER = field(AbstractFurnaceBlockEntity.class, "cookingTimer");
        private static final @Nullable Field COOKING_TOTAL_TIME = field(AbstractFurnaceBlockEntity.class, "cookingTotalTime");

        @Override
        public void appendData(IDataWriter data, IServerAccessor<AbstractFurnaceBlockEntity> accessor, IPluginConfig config) {
            if (COOKING_TIMER == null || COOKING_TOTAL_TIME == null) return;

            data.add(ProgressData.TYPE, res -> {
                var furnace = accessor.getTarget();
                if (furnace.getBlockState().getValue(AbstractFurnaceBlock.LIT)) {
                    try {
                        int timer = COOKING_TIMER.getInt(furnace);
                        int total = COOKING_TOTAL_TIME.getInt(furnace);
                        res.add(ProgressData
                            .tick(timer, total)
                            .itemGetter(furnace::getItem)
                            .input(0, 1)
                            .output(2));
                    } catch (IllegalAccessException e) {
                        // no-op
                    }
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    enum Beacon implements IDataProvider<BeaconBlockEntity> {
        INSTANCE;

        private static final @Nullable Field PRIMARY_POWER = field(BeaconBlockEntity.class, "primaryPower");
        private static final @Nullable Field SECONDARY_POWER = field(BeaconBlockEntity.class, "secondaryPower");
        private static final @Nullable Field LEVELS = field(BeaconBlockEntity.class, "levels");

        @Override
        public void appendData(IDataWriter data, IServerAccessor<BeaconBlockEntity> accessor, IPluginConfig config) {
            if (PRIMARY_POWER == null || SECONDARY_POWER == null || LEVELS == null) return;

            if (config.getBoolean(Options.EFFECT_BEACON)) data.add(BeaconDataProvider.DATA, res -> {
                try {
                    var beacon = accessor.getTarget();
                    var primary = (Holder<MobEffect>) PRIMARY_POWER.get(beacon);
                    var secondary = (Holder<MobEffect>) SECONDARY_POWER.get(beacon);
                    int levels = LEVELS.getInt(beacon);
                    res.add(new BeaconDataProvider.Data(primary, levels >= 4 ? secondary : null));
                } catch (IllegalAccessException e) {
                    // no-op
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    enum Beehive implements IDataProvider<BeehiveBlockEntity> {
        INSTANCE;

        private static final @Nullable Field STORED = field(BeehiveBlockEntity.class, "stored");
        private static final @Nullable Field BEE_DATA_OCCUPANT;

        static {
            Field occupantField = null;
            try {
                Class<?> beeDataClass = null;
                for (var inner : BeehiveBlockEntity.class.getDeclaredClasses()) {
                    if (inner.getSimpleName().equals("BeeData")) {
                        beeDataClass = inner;
                        break;
                    }
                }
                if (beeDataClass != null) {
                    occupantField = beeDataClass.getDeclaredField("occupant");
                    occupantField.setAccessible(true);
                } else {
                    LOG.warn("Failed to find BeehiveBlockEntity.BeeData inner class, beehive provider will be a no-op");
                }
            } catch (Exception e) {
                LOG.warn("Failed to resolve BeehiveBlockEntity.BeeData.occupant, beehive provider will be a no-op: {}", e.getMessage());
            }
            BEE_DATA_OCCUPANT = occupantField;
        }

        @Override
        public void appendData(IDataWriter data, IServerAccessor<BeehiveBlockEntity> accessor, IPluginConfig config) {
            if (STORED == null || BEE_DATA_OCCUPANT == null) return;

            if (config.getBoolean(Options.BEE_HIVE_OCCUPANTS)) {
                try {
                    var stored = (List<?>) STORED.get(accessor.getTarget());
                    if (stored != null && !stored.isEmpty()) {
                        var occupants = new ArrayList<BeehiveDataProvider.OccupantsData.Occupant>(stored.size());

                        for (var beeData : stored) {
                            var occupant = (BeehiveBlockEntity.Occupant) BEE_DATA_OCCUPANT.get(beeData);
                            var entityType = occupant.entityData().type();
                            var beeNbt = occupant.entityData().copyTagWithoutId();
                            var customName = beeNbt.getString("CustomName").orElse(null);
                            occupants.add(new BeehiveDataProvider.OccupantsData.Occupant(entityType, customName));
                        }

                        data.addImmediate(new BeehiveDataProvider.OccupantsData(occupants));
                    }
                } catch (IllegalAccessException e) {
                    // no-op
                }
            }
        }
    }

    enum Lectern implements IDataProvider<LecternBlockEntity> {
        INSTANCE;

        private static final @Nullable Field PAGE_COUNT = field(LecternBlockEntity.class, "pageCount");

        @Override
        public void appendData(IDataWriter data, IServerAccessor<LecternBlockEntity> accessor, IPluginConfig config) {
            if (PAGE_COUNT == null) return;
            if (!config.getBoolean(Options.BOOK_LECTERN)) return;

            var lectern = accessor.getTarget();
            if (!lectern.hasBook()) return;

            try {
                int pageCount = PAGE_COUNT.getInt(lectern);
                data.addImmediate(new LecternDataProvider.Data(lectern.getBook(), lectern.getPage() + 1, pageCount));
            } catch (IllegalAccessException e) {
                // no-op
            }
        }
    }

    enum BaseContainer implements IDataProvider<BaseContainerBlockEntity> {
        INSTANCE;

        private static final @Nullable Field LOCK_KEY = field(BaseContainerBlockEntity.class, "lockKey");

        @Override
        public void appendData(IDataWriter data, IServerAccessor<BaseContainerBlockEntity> accessor, IPluginConfig config) {
            if (LOCK_KEY == null) return;

            try {
                var lockKey = (LockCode) LOCK_KEY.get(accessor.getTarget());
                if (!lockKey.unlocksWith(accessor.getPlayer().getMainHandItem())) {
                    data.blockAll(ItemData.TYPE);
                }
            } catch (IllegalAccessException e) {
                // no-op
            }
        }
    }

}
