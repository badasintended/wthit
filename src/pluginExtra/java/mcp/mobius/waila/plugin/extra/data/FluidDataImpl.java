package mcp.mobius.waila.plugin.extra.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class FluidDataImpl extends FluidData.PlatformDependant<Object> {

    private static final StreamCodec<RegistryFriendlyByteBuf, Fluid> FLUID_CODEC = ByteBufCodecs.registry(Registries.FLUID);
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidDataImpl> CODEC = StreamCodec.ofMember((d, buf) -> {
        buf.writeEnum(d.unit);
        buf.writeVarInt(d.entries.size());

        for (var entry : d.entries) {
            if (entry.isEmpty()) {
                buf.writeBoolean(true);
            } else {
                buf.writeBoolean(false);
                FLUID_CODEC.encode(buf, entry.fluid);
                DataComponentPatch.STREAM_CODEC.encode(buf, entry.data);
                buf.writeDouble(entry.stored);
                buf.writeDouble(entry.capacity);
            }
        }
    }, buf -> {
        var unit = buf.readEnum(Unit.class);
        var size = buf.readVarInt();

        var d = new FluidDataImpl(null, unit, size);
        for (var i = 0; i < size; i++) {
            if (buf.readBoolean()) continue;

            var fluid = FLUID_CODEC.decode(buf);
            var data = DataComponentPatch.STREAM_CODEC.decode(buf);
            var stored = buf.readDouble();
            var capacity = buf.readDouble();
            d.add(fluid, data, stored, capacity);
        }

        return d;
    });

    private final List<Entry<?>> entries;
    private final PlatformTranslator<Object> proxy;
    private final Unit unit;

    public FluidDataImpl(@Nullable FluidData.PlatformTranslator<Object> proxy, Unit unit, int slotCountHint) {
        this.entries = slotCountHint == -1 ? new ArrayList<>() : new ArrayList<>(slotCountHint);
        this.proxy = proxy;
        this.unit = unit;
    }

    @Override
    protected void implAdd(Fluid fluid, DataComponentPatch data, double stored, double capacity) {
        entries.add(new Entry<>(fluid, data, stored, capacity));
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    protected PlatformTranslator<Object> translator() {
        return proxy;
    }

    @Override
    public Type<? extends IData> type() {
        return TYPE;
    }

    public Unit unit() {
        return unit;
    }

    public List<Entry<?>> entries() {
        return entries;
    }

    public static class Entry<T extends Fluid> implements FluidDescriptionContext<T> {

        private final T fluid;
        private final DataComponentPatch data;
        private final double stored;
        private final double capacity;

        private Entry(T fluid, DataComponentPatch data, double stored, double capacity) {
            this.fluid = fluid;
            this.data = data;
            this.stored = stored;
            this.capacity = capacity;
        }

        public boolean isEmpty() {
            return fluid == Fluids.EMPTY || stored <= 0;
        }

        @Override
        public T fluid() {
            return fluid;
        }

        @Override
        public DataComponentPatch data() {
            return data;
        }

        public double stored() {
            return stored;
        }

        public double capacity() {
            return capacity;
        }

    }

    public static class FluidDescription implements FluidData.FluidDescription {

        public static final Map<Fluid, FluidDescriptor<Fluid>> FLUID_STATIC = new HashMap<>();
        public static final Map<Class<?>, FluidDescriptor<Fluid>> FLUID_DYNAMIC = new HashMap<>();
        public static final Map<Block, CauldronDescriptor> CAULDRON_STATIC = new HashMap<>();
        public static final Map<Class<?>, CauldronDescriptor> CAULDRON_DYNAMIC = new HashMap<>();

        private static final FluidDescription INSTANCE = new FluidDescription();

        private static final Component UNKNOWN_FLUID_NAME = Component.translatable(Tl.Tooltip.Extra.UNKNOWN_FLUID);

        private static final FluidDescriptor<Fluid> UNKNOWN_FLUID_DESC = (ctx, desc) -> {
        };
        private static final CauldronDescriptor NULL_CAULDRON_DESC = state -> null;

        private TextureAtlasSprite sprite;
        private int tint;
        private Component name;

        @SuppressWarnings("unchecked")
        public static FluidDescription getFluidDesc(Entry<?> entry) {
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

            INSTANCE.sprite(Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).particleIcon())
                .tint(0xFFFFFFFF)
                .name(UNKNOWN_FLUID_NAME);

            descriptor.describeFluid((Entry<Fluid>) entry, INSTANCE);
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

}
