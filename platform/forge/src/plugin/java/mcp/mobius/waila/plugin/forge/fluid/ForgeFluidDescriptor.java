package mcp.mobius.waila.plugin.forge.fluid;

import mcp.mobius.waila.api.data.FluidData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

public enum ForgeFluidDescriptor implements FluidData.Descriptor<Fluid> {

    INSTANCE;

    @Override
    public void describe(FluidData.DescriptionContext<Fluid> ctx, FluidData.Description desc) {
        FluidStack stack = new FluidStack(ctx.fluid(), 1, ctx.nbt());
        FluidType type = ctx.fluid().getFluidType();
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(type);

        desc.name(type.getDescription(stack))
            .sprite(Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture(stack)))
            .tint(extensions.getTintColor(stack));
    }

}
