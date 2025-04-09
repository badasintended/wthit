package mcp.mobius.waila.plugin.vanilla.fluid;

import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.api.data.FluidData.CauldronDescriptor;
import mcp.mobius.waila.api.data.FluidData.FluidDescription;
import mcp.mobius.waila.api.data.FluidData.FluidDescriptionContext;
import mcp.mobius.waila.api.data.FluidData.FluidDescriptor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluids;

public enum WaterDescriptor implements FluidDescriptor<FlowingFluid>, CauldronDescriptor {

    INSTANCE;

    private static final Component NAME = Component.translatable("block.minecraft.water");

    @Override
    public void describeFluid(FluidDescriptionContext<FlowingFluid> ctx, FluidDescription desc) {
        desc.name(NAME)
            .sprite(Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).particleIcon())
            .tint(0xFF3F76E4);
    }

    @Override
    public FluidData getCauldronFluidData(BlockState state) {
        return FluidData.of(FluidData.Unit.MILLIBUCKETS, 1)
            .add(Fluids.WATER, DataComponentPatch.EMPTY, state.getValue(LayeredCauldronBlock.LEVEL) * 1000 / 3.0, 1000);
    }

}
