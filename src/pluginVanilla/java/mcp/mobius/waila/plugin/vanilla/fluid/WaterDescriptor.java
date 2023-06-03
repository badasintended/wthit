package mcp.mobius.waila.plugin.vanilla.fluid;

import mcp.mobius.waila.api.data.FluidData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FlowingFluid;

public enum WaterDescriptor implements FluidData.Descriptor<FlowingFluid> {

    INSTANCE;

    private static final Component NAME = Component.translatable("block.minecraft.water");

    @Override
    public void describe(FluidData.DescriptionContext<FlowingFluid> ctx, FluidData.Description desc) {
        desc.name(NAME)
            .sprite(Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon())
            .tint(0xFF3F76E4);
    }

}
