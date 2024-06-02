package mcp.mobius.waila.plugin.textile.fluid;

import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.api.data.FluidData.CauldronDescriptor;
import mcp.mobius.waila.api.data.FluidData.FluidDescription;
import mcp.mobius.waila.api.data.FluidData.FluidDescriptionContext;
import mcp.mobius.waila.api.data.FluidData.FluidDescriptor;
import mcp.mobius.waila.api.fabric.FabricFluidData;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.CauldronFluidContent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public enum TextileFluidDescriptor implements FluidDescriptor<Fluid>, CauldronDescriptor {

    INSTANCE;

    @Override
    public void describeFluid(FluidDescriptionContext<Fluid> ctx, FluidDescription desc) {
        var variant = FluidVariant.of(ctx.fluid(), ctx.data());
        desc.name(FluidVariantAttributes.getName(variant));

        var sprite = FluidVariantRendering.getSprite(variant);
        if (sprite != null) {
            desc.sprite(sprite)
                .tint(FluidVariantRendering.getColor(variant));
        }
    }

    @Override
    public @Nullable FluidData getCauldronFluidData(BlockState state) {
        var content = CauldronFluidContent.getForBlock(state.getBlock());
        if (content == null || content.fluid == Fluids.EMPTY) return null;

        double stored = content.currentLevel(state) * content.amountPerLevel;
        double capacity = content.maxLevel * content.amountPerLevel;

        return FabricFluidData.of(1)
            .add(content.fluid, DataComponentPatch.EMPTY, stored, capacity);
    }

}
