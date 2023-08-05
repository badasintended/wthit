package mcp.mobius.waila.network.s2c;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BlacklistSyncS2CPacket implements Packet.S2C<BlacklistSyncS2CPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("blacklist");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        return new Payload(readIds(buf), readIds(buf), readIds(buf));
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, Payload payload, PacketSender responseSender) {
        BlacklistConfig blacklist = Waila.BLACKLIST_CONFIG.get();
        setBlackList(payload.blockIds, blacklist.blocks, BuiltInRegistries.BLOCK);
        setBlackList(payload.blockEntityIds, blacklist.blockEntityTypes, BuiltInRegistries.BLOCK_ENTITY_TYPE);
        setBlackList(payload.entityIds, blacklist.entityTypes, BuiltInRegistries.ENTITY_TYPE);
    }

    public static <T> void setBlackList(Set<ResourceLocation> ids, Set<T> set, Registry<T> registry) {
        for (ResourceLocation id : ids) {
            set.add(registry.get(id));
        }
    }

    public static void writeIds(FriendlyByteBuf buf, Set<ResourceLocation> set) {
        Map<String, List<ResourceLocation>> groups = set.stream().collect(Collectors.groupingBy(ResourceLocation::getNamespace));
        buf.writeVarInt(groups.size());
        groups.forEach((namespace, ids) -> {
            buf.writeUtf(namespace);
            buf.writeVarInt(ids.size());
            ids.forEach(id -> buf.writeUtf(id.getPath()));
        });
    }

    public static Set<ResourceLocation> readIds(FriendlyByteBuf buf) {
        Set<ResourceLocation> set = new HashSet<>();
        int groupSize = buf.readVarInt();
        for (int i = 0; i < groupSize; i++) {
            String namespace = buf.readUtf();
            int groupLen = buf.readVarInt();
            for (int j = 0; j < groupLen; j++) {
                set.add(new ResourceLocation(namespace, buf.readUtf()));
            }
        }
        return set;
    }

    public record Payload(
        Set<ResourceLocation> blockIds,
        Set<ResourceLocation> blockEntityIds,
        Set<ResourceLocation> entityIds
    ) implements CustomPacketPayload {

        @Override
        public void write(@NotNull FriendlyByteBuf buf) {
            writeIds(buf, blockIds);
            writeIds(buf, blockEntityIds);
            writeIds(buf, entityIds);
        }

        @Override
        public @NotNull ResourceLocation id() {
            return ID;
        }

    }

}
