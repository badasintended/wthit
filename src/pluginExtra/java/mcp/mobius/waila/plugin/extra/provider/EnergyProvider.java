package mcp.mobius.waila.plugin.extra.provider;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.BarComponent;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.WrappedComponent;
import mcp.mobius.waila.api.data.EnergyData;
import mcp.mobius.waila.api.util.WNumbers;
import mcp.mobius.waila.plugin.extra.data.EnergyDataImpl;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class EnergyProvider extends DataProvider<EnergyData, EnergyDataImpl> {

    public static final EnergyProvider INSTANCE = new EnergyProvider();

    private static final String INFINITE = "∞";

    private static final ResourceLocation INFINITE_TAG_ID = ResourceLocation.fromNamespaceAndPath(WailaConstants.NAMESPACE, "extra/infinite_energy");
    private static final TagKey<Block> INFINITE_BLOCK_TAG = TagKey.create(Registries.BLOCK, INFINITE_TAG_ID);
    private static final TagKey<BlockEntityType<?>> INFINITE_BLOCK_ENTITY_TAG = TagKey.create(Registries.BLOCK_ENTITY_TYPE, INFINITE_TAG_ID);
    private static final TagKey<EntityType<?>> INFINITE_ENTITY_TAG = TagKey.create(Registries.ENTITY_TYPE, INFINITE_TAG_ID);

    private EnergyProvider() {
        super(EnergyData.TYPE, EnergyDataImpl.CODEC);
    }

    @Override
    protected void registerAdditions(ICommonRegistrar registrar, int priority) {
        registrar.blockData(new InfiniteEnergyBlockProvider(), BlockEntity.class, 1);
        registrar.entityData(new InfiniteEnergyEntityProvider(), Entity.class, 1);
    }

    @Override
    protected void appendBody(ITooltip tooltip, EnergyDataImpl energy, IPluginConfig config, ResourceLocation objectId) {
        var desc = EnergyDataImpl.Description.get(objectId.getNamespace());

        var stored = energy.stored();
        var capacity = energy.capacity();
        var ratio = Double.isInfinite(capacity) ? 1f : (float) (stored / capacity);

        var unit = desc.unit();
        var name = desc.name();
        var color = desc.color();

        String text;
        if (Double.isInfinite(stored)) text = INFINITE;
        else {
            text = WNumbers.suffix((long) stored);
            if (Double.isFinite(capacity)) text += "/" + WNumbers.suffix((long) capacity);
        }

        if (!unit.isEmpty()) text += " " + unit;

        tooltip.setLine(EnergyData.ID, new PairComponent(
            new WrappedComponent(name),
            new BarComponent(ratio, 0xFF000000 | color, text)));
    }

    private static class InfiniteEnergyBlockProvider implements IDataProvider<BlockEntity> {

        @Override
        public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
            data.add(EnergyData.TYPE, res -> {
                var target = accessor.getTarget();

                if (target.getBlockState().is(INFINITE_BLOCK_TAG)
                    || BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(target.getType()).is(INFINITE_BLOCK_ENTITY_TAG)) {
                    res.add(EnergyData.INFINITE);
                }
            });
        }

    }

    private static class InfiniteEnergyEntityProvider implements IDataProvider<Entity> {

        @Override
        public void appendData(IDataWriter data, IServerAccessor<Entity> accessor, IPluginConfig config) {
            data.add(EnergyData.TYPE, res -> {
                if (accessor.getTarget().getType().is(INFINITE_ENTITY_TAG)) {
                    res.add(EnergyData.INFINITE);
                }
            });
        }

    }

}
