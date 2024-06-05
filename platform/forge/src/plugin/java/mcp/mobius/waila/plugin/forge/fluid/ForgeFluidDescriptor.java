package mcp.mobius.waila.plugin.forge.fluid;

import mcp.mobius.waila.api.data.FluidData.FluidDescription;
import mcp.mobius.waila.api.data.FluidData.FluidDescriptionContext;
import mcp.mobius.waila.api.data.FluidData.FluidDescriptor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public enum ForgeFluidDescriptor implements FluidDescriptor<Fluid> {

    INSTANCE;

    @Override
    @SuppressWarnings("deprecation")
    public void describeFluid(FluidDescriptionContext<Fluid> ctx, FluidDescription desc) {
        var customData = ctx.data().get(DataComponents.CUSTOM_DATA);
        var nbt = customData != null && customData.isPresent() ? customData.get().getUnsafe() : null;
        var stack = new FluidStack(ctx.fluid(), 1, nbt);
        var type = ctx.fluid().getFluidType();
        var extensions = IClientFluidTypeExtensions.of(type);

        desc.name(type.getDescription(stack))
            .sprite(Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture(stack)))
            .tint(extensions.getTintColor(stack));
    }

}
