package mcp.mobius.waila.plugin.neo.fluid;

import mcp.mobius.waila.api.data.FluidData.FluidDescription;
import mcp.mobius.waila.api.data.FluidData.FluidDescriptionContext;
import mcp.mobius.waila.api.data.FluidData.FluidDescriptor;
import net.minecraft.client.Minecraft;
import net.minecraft.data.AtlasIds;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public enum NeoFluidDescriptor implements FluidDescriptor<Fluid> {

    INSTANCE;

    @Override
    public void describeFluid(FluidDescriptionContext<Fluid> ctx, FluidDescription desc) {
        var stack = new FluidStack(ctx.fluid().builtInRegistryHolder(), 1, ctx.data());
        var type = ctx.fluid().getFluidType();
        var model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(ctx.fluid().defaultFluidState());
        var tintSource = model.fluidTintSource();

        desc.name(type.getDescription(stack))
            .sprite(model.stillMaterial().sprite())
            .tint(tintSource == null ? 0xFFFFFFFF : tintSource.colorAsStack(stack));
    }

}
