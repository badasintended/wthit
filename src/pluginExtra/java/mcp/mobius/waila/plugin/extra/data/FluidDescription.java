package mcp.mobius.waila.plugin.extra.data;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class FluidDescription implements FluidData.FluidDescription {

    public static final Map<Fluid, FluidData.FluidDescriptor<Fluid>> FLUID_STATIC = new HashMap<>();
    public static final Map<Class<?>, FluidData.FluidDescriptor<Fluid>> FLUID_DYNAMIC = new HashMap<>();
    public static final Map<Block, FluidData.CauldronDescriptor> CAULDRON_STATIC = new HashMap<>();
    public static final Map<Class<?>, FluidData.CauldronDescriptor> CAULDRON_DYNAMIC = new HashMap<>();

    private static final FluidDescription INSTANCE = new FluidDescription();

    private static final Component UNKNOWN_FLUID_NAME = Component.translatable(Tl.Tooltip.Extra.UNKNOWN_FLUID);

    private static final FluidData.FluidDescriptor<Fluid> UNKNOWN_FLUID_DESC = (ctx, desc) -> {};
    private static final FluidData.CauldronDescriptor NULL_CAULDRON_DESC = state -> null;

    private TextureAtlasSprite sprite;
    private int tint;
    private Component name;

    @SuppressWarnings("unchecked")
    public static FluidDescription getFluidDesc(FluidData.Entry<?> entry) {
        var fluid = entry.fluid();
        var descriptor = FLUID_STATIC.get(fluid);

        if (descriptor == null) {
            Class<?> clazz = fluid.getClass();

            while (clazz != null && clazz != Object.class) {
                descriptor = FLUID_DYNAMIC.get(clazz);

                if (descriptor != null) {
                    FLUID_DYNAMIC.put(fluid.getClass(), descriptor);
                    break;
                }

                clazz = clazz.getSuperclass();
            }
        }

        if (descriptor == null) {
            descriptor = UNKNOWN_FLUID_DESC;
            FLUID_STATIC.put(fluid, UNKNOWN_FLUID_DESC);
        }

        INSTANCE.sprite(Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon())
            .tint(0xFFFFFFFF)
            .name(UNKNOWN_FLUID_NAME);

        descriptor.describeFluid((FluidData.Entry<Fluid>) entry, INSTANCE);
        return INSTANCE;
    }

    @Nullable
    public static FluidData getCauldronFluidData(BlockState state) {
        var block = state.getBlock();
        var getter = CAULDRON_STATIC.get(block);

        if (getter == null) {
            Class<?> clazz = block.getClass();

            while (clazz != null && clazz != Object.class) {
                getter = CAULDRON_DYNAMIC.get(clazz);

                if (getter != null) {
                    CAULDRON_DYNAMIC.put(block.getClass(), getter);
                    break;
                }

                clazz = clazz.getSuperclass();
            }
        }

        if (getter == null) {
            getter = NULL_CAULDRON_DESC;
            CAULDRON_STATIC.put(block, NULL_CAULDRON_DESC);
        }

        return getter.getCauldronFluidData(state);
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
