package mcp.mobius.waila.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PacketReader {

    public static <T> T receiveData(PacketByteBuf buf, Function<CompoundTag, T> fun) {
        return fun.apply(buf.readCompoundTag());
    }

    public static <T> T sendConfig(PacketByteBuf buf, Function<Map<Identifier, Boolean>, T> fun) {
        int size = buf.readInt();
        Map<Identifier, Boolean> temp = new HashMap<>();
        for (int j = 0; j < size; j++) {
            int idLength = buf.readInt();
            Identifier id = new Identifier(buf.readString(idLength));
            boolean value = buf.readBoolean();
            temp.put(id, value);
        }
        return fun.apply(temp);
    }

    public static <T> T requestEntity(PacketByteBuf buf, Function<Integer, T> fun) {
        return fun.apply(buf.readInt());
    }

    public static <T> T requestBlockEntity(PacketByteBuf buf, Function<BlockPos, T> fun) {
        return fun.apply(buf.readBlockPos());
    }

}
