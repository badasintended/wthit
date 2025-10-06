package mcp.mobius.waila.plugin.extra.data;

import java.util.ArrayList;

import mcp.mobius.waila.api.data.ProgressData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ProgressDataImpl extends ProgressData {

    @Override
    public void write(FriendlyByteBuf buf) {
        var d = this;
        if (d.hasTick) {
            buf.writeVarInt(d.currentTick);
            buf.writeVarInt(d.maxTick);
        } else {
            buf.writeFloat(d.ratio);
        }

        buf.writeVarInt(d.input.size());
        for (var stack : d.input) {
            buf.writeItem(stack);
        }

        buf.writeVarInt(d.output.size());
        for (var stack : d.output) {
            buf.writeItem(stack);
        }
    }

    public static ProgressDataImpl of(FriendlyByteBuf buf) {
        var hasTick = buf.readBoolean();
        var d = hasTick
            ? new ProgressDataImpl(buf.readVarInt(), buf.readVarInt())
            : new ProgressDataImpl(buf.readFloat());

        var inputSize = buf.readVarInt();
        d.input.ensureCapacity(inputSize);
        for (var i = 0; i < inputSize; i++) {
            d.input.add(buf.readItem());
        }

        var outputSize = buf.readVarInt();
        d.output.ensureCapacity(outputSize);
        for (var i = 0; i < outputSize; i++) {
            d.output.add(buf.readItem());
        }

        return d;
    }

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

}
