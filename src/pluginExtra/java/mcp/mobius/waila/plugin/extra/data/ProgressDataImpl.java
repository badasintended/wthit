package mcp.mobius.waila.plugin.extra.data;

import java.util.ArrayList;

import mcp.mobius.waila.api.data.ProgressData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ProgressDataImpl extends ProgressData {

    private final float ratio;

    public ProgressDataImpl(float ratio) {
        this.ratio = ratio;
    }

    public ProgressDataImpl(FriendlyByteBuf buf) {
        this.ratio = buf.readFloat();

        var inputSize = buf.readVarInt();
        input.ensureCapacity(inputSize);
        for (var i = 0; i < inputSize; i++) {
            input.add(buf.readItem());
        }

        var outputSize = buf.readVarInt();
        output.ensureCapacity(outputSize);
        for (var i = 0; i < outputSize; i++) {
            output.add(buf.readItem());
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(ratio);

        buf.writeVarInt(input.size());
        for (var stack : input) {
            buf.writeItem(stack);
        }

        buf.writeVarInt(output.size());
        for (var stack : output) {
            buf.writeItem(stack);
        }
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

}
