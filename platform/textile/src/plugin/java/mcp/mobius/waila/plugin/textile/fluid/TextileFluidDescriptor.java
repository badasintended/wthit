package mcp.mobius.waila.plugin.textile.fluid;

import mcp.mobius.waila.api.data.FluidData;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.Fluid;

@SuppressWarnings("UnstableApiUsage")
public enum TextileFluidDescriptor implements FluidData.Descriptor<Fluid> {

    INSTANCE;

    @Override
    public void describe(FluidData.DescriptionContext<Fluid> ctx, FluidData.Description desc) {
        FluidVariant variant = FluidVariant.of(ctx.fluid(), ctx.nbt());
        desc.name(FluidVariantAttributes.getName(variant));

        TextureAtlasSprite sprite = FluidVariantRendering.getSprite(variant);
        if (sprite != null) {
            desc.sprite(sprite)
                .tint(FluidVariantRendering.getColor(variant));
        }
    }

}
