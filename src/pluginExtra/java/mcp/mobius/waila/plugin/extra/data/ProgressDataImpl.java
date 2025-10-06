package mcp.mobius.waila.plugin.extra.data;

import java.util.ArrayList;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.data.ProgressData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class ProgressDataImpl extends ProgressData {

    public static final StreamCodec<RegistryFriendlyByteBuf, ProgressDataImpl> CODEC = StreamCodec.ofMember((d, buf) -> {
        buf.writeBoolean(d.hasTick);
        if (d.hasTick) {
            buf.writeVarInt(d.currentTick);
            buf.writeVarInt(d.maxTick);
        } else {
            buf.writeFloat(d.ratio);
        }

        buf.writeVarInt(d.input.size());
        for (var stack : d.input) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
        }

        buf.writeVarInt(d.output.size());
        for (var stack : d.output) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
        }
    }, buf -> {
        var hasTick = buf.readBoolean();
        var d = hasTick
            ? new ProgressDataImpl(buf.readVarInt(), buf.readVarInt())
            : new ProgressDataImpl(buf.readFloat());

        var inputSize = buf.readVarInt();
        d.input.ensureCapacity(inputSize);
        for (var i = 0; i < inputSize; i++) {
            d.input.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
        }

        var outputSize = buf.readVarInt();
        d.output.ensureCapacity(outputSize);
        for (var i = 0; i < outputSize; i++) {
            d.output.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
        }

        return d;
    });

    public final boolean hasTick;
    public float ratio;
    public int currentTick, maxTick;

    public ProgressDataImpl(float ratio) {
        this.hasTick = false;
        this.ratio = ratio;
    }

    public ProgressDataImpl(int currentTick, int maxTick) {
        this.hasTick = true;
        this.currentTick = currentTick;
        this.maxTick = maxTick;
    }

    public ArrayList<ItemStack> input() {
        return input;
    }

    public ArrayList<ItemStack> output() {
        return output;
    }

    @Override
    public Type<? extends IData> type() {
        return TYPE;
    }

}
