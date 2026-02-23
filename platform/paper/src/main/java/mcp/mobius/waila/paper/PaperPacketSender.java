package mcp.mobius.waila.paper;

import java.util.logging.Logger;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.access.DataType;
import mcp.mobius.waila.network.play.s2c.RawDataResponsePlayS2CPacket;
import mcp.mobius.waila.network.play.s2c.TypedDataResponsePlayS2CPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Adapts BadPackets' {@link PacketSender} to Bukkit's plugin messaging API.
 * <p>
 * Note: {@code PacketSender} is {@code @ApiStatus.NonExtendable}, meaning the library author
 * may add new abstract methods in future versions. This implementation only needs
 * {@link #canSend(Identifier)} and {@link #send(CustomPacketPayload, ChannelFutureListener)},
 * which are the two methods called by {@code DataWriter.SERVER.send()}. If BadPackets adds
 * new abstract methods, this class will need to be updated.
 */
public class PaperPacketSender implements PacketSender {

    private final Plugin plugin;
    private final Player bukkitPlayer;
    private final ServerPlayer serverPlayer;
    private final Logger logger;

    public PaperPacketSender(Plugin plugin, Player bukkitPlayer, ServerPlayer serverPlayer) {
        this.plugin = plugin;
        this.bukkitPlayer = bukkitPlayer;
        this.serverPlayer = serverPlayer;
        this.logger = plugin.getLogger();
    }

    @Override
    public boolean canSend(Identifier id) {
        return id.equals(RawDataResponsePlayS2CPacket.TYPE.id())
            || id.equals(TypedDataResponsePlayS2CPacket.ID);
    }

    @Override
    public void send(CustomPacketPayload payload, @Nullable ChannelFutureListener callback) {
        if (callback != null) {
            throw new UnsupportedOperationException("PaperPacketSender does not support send callbacks");
        }

        if (payload instanceof RawDataResponsePlayS2CPacket.Payload rawPayload) {
            var buf = new FriendlyByteBuf(Unpooled.buffer());
            try {
                buf.writeNbt(rawPayload.data());
                sendBytes(PaperWaila.CHANNEL_DATA_RAW, buf);
            } finally {
                buf.release();
            }
        } else if (payload instanceof TypedDataResponsePlayS2CPacket.Payload typedPayload) {
            var registryAccess = serverPlayer.level().registryAccess();
            var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess);
            try {
                DataType.CODEC.encode(buf, typedPayload.data());
                sendBytes(PaperWaila.CHANNEL_DATA_TYPED, buf);
            } finally {
                buf.release();
            }
        } else {
            logger.warning("[WTHIT] Unknown payload type: " + payload.getClass().getName());
        }
    }

    private void sendBytes(String channel, FriendlyByteBuf buf) {
        var bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        bukkitPlayer.sendPluginMessage(plugin, channel, bytes);
    }

}
