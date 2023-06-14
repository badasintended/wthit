package mcp.mobius.waila.plugin.extra.data;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;

public class FluidDescription implements FluidData.Description {

    public static final Map<Fluid, FluidData.Descriptor<Fluid>> STATIC = new HashMap<>();
    public static final Map<Class<?>, FluidData.Descriptor<Fluid>> DYNAMIC = new HashMap<>();

    private static final FluidDescription INSTANCE = new FluidDescription();

    private static final Component UNKNOWN_FLUID_NAME = Component.translatable(Tl.Tooltip.Extra.UNKNOWN_FLUID);
    private static final FluidData.Descriptor<Fluid> UNKNOWN = (ctx, desc) -> {};

    private TextureAtlasSprite sprite;
    private int tint;
    private Component name;

    @SuppressWarnings("unchecked")
    public static FluidDescription get(FluidData.Entry<?> entry) {
        Fluid fluid = entry.fluid();
        FluidData.Descriptor<Fluid> descriptor = STATIC.get(fluid);

        if (descriptor == null) {
            Class<?> clazz = fluid.getClass();

            while (clazz != null && clazz != Object.class) {
                descriptor = DYNAMIC.get(clazz);

                if (descriptor != null) {
                    DYNAMIC.put(fluid.getClass(), descriptor);
                    break;
                }

                clazz = clazz.getSuperclass();
            }
        }

        if (descriptor == null) {
            descriptor = UNKNOWN;
            STATIC.put(fluid, UNKNOWN);
        }

        INSTANCE.sprite(Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon())
            .tint(0xFFFFFFFF)
            .name(UNKNOWN_FLUID_NAME);

        descriptor.describe((FluidData.Entry<Fluid>) entry, INSTANCE);
        return INSTANCE;
    }

    public Component name() {
        return name;
    }

    public TextureAtlasSprite sprite() {
        return sprite;
    }

    public int tint() {
        return tint;
    }

    @Override
    public FluidDescription name(Component name) {
        this.name = name;
        return this;
    }

    @Override
    public FluidDescription sprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
        return this;
    }

    @Override
    public FluidDescription tint(int argb) {
        this.tint = argb;
        return this;
    }

}
