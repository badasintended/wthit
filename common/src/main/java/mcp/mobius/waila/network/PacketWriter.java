package mcp.mobius.waila.network;

import java.util.Set;

import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;


public class PacketWriter {

    public static PacketByteBuf receiveData(PacketByteBuf buf, CompoundTag tag) {
        buf.writeCompoundTag(tag);
        return buf;
    }

    public static PacketByteBuf sendConfig(PacketByteBuf buf, PluginConfig config) {
        Set<ConfigEntry> entries = config.getSyncableConfigs();
        buf.writeInt(entries.size());
        entries.forEach(e -> {
            buf.writeInt(e.getId().toString().length());
            buf.writeString(e.getId().toString());
            buf.writeBoolean(e.getValue());
        });
        return buf;
    }

    public static PacketByteBuf requestEntity(PacketByteBuf buf, Entity entity) {
        buf.writeInt(entity.getId());
        return buf;
    }

    public static PacketByteBuf requestBlockEntity(PacketByteBuf buf, BlockEntity blockEntity) {
        buf.writeBlockPos(blockEntity.getPos());
        return buf;
    }

}
