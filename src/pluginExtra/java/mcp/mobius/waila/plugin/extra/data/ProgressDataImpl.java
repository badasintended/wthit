package mcp.mobius.waila.plugin.extra.data;

import java.util.ArrayList;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.data.ProgressData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class ProgressDataImpl extends ProgressData {

    public static final StreamCodec<RegistryFriendlyByteBuf, ProgressDataImpl> CODEC = StreamCodec.ofMember((d, buf) -> {
        buf.writeFloat(d.ratio);

        buf.writeVarInt(d.input.size());
        for (var stack : d.input) {
            ItemStack.STREAM_CODEC.encode(buf, stack);
        }

        buf.writeVarInt(d.output.size());
        for (var stack : d.output) {
            ItemStack.STREAM_CODEC.encode(buf, stack);
        }
    }, buf -> {
        var ratio = buf.readFloat();
        var d = new ProgressDataImpl(ratio);

        var inputSize = buf.readVarInt();
        d.input.ensureCapacity(inputSize);
        for (var i = 0; i < inputSize; i++) {
            d.input.add(ItemStack.STREAM_CODEC.decode(buf));
        }

        var outputSize = buf.readVarInt();
        d.output.ensureCapacity(outputSize);
        for (var i = 0; i < outputSize; i++) {
            d.output.add(ItemStack.STREAM_CODEC.decode(buf));
        }

        return d;
    });

    private final float ratio;

    public ProgressDataImpl(float ratio) {
        this.ratio = ratio;
    }

    public float ratio() {
        return ratio;
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
