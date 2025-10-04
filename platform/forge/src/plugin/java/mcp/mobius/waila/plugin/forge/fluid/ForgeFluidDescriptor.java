package mcp.mobius.waila.plugin.forge.fluid;

import mcp.mobius.waila.api.data.FluidData.FluidDescription;
import mcp.mobius.waila.api.data.FluidData.FluidDescriptionContext;
import mcp.mobius.waila.api.data.FluidData.FluidDescriptor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.AtlasIds;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public enum ForgeFluidDescriptor implements FluidDescriptor<Fluid> {

    INSTANCE;

    @Override
    public void describeFluid(FluidDescriptionContext<Fluid> ctx, FluidDescription desc) {
        var customData = ctx.data().get(DataComponents.CUSTOM_DATA);
        var nbt = customData != null && customData.isPresent() ? customData.get().copyTag() : null;
        var stack = new FluidStack(ctx.fluid(), 1);
        stack.setTag(nbt);

        var type = ctx.fluid().getFluidType();
        var extensions = IClientFluidTypeExtensions.of(type);
        var atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);

        desc.name(type.getDescription(stack))
            .sprite(atlas.getSprite(extensions.getStillTexture(stack)))
            .tint(extensions.getTintColor(stack));
    }

}
