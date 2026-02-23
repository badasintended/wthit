package lol.bai.badpackets.impl.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import lol.bai.badpackets.api.PacketReceiver;
import lol.bai.badpackets.api.PacketSender;
import lol.bai.badpackets.impl.Constants;
import lol.bai.badpackets.impl.payload.UntypedPayload;
import lol.bai.badpackets.impl.platform.PlatformProxy;
import lol.bai.badpackets.impl.registry.ChannelRegistry;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.util.thread.BlockableEventLoop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractPacketHandler<C, B extends FriendlyByteBuf> implements PacketSender {

    protected final ChannelRegistry<B, C> registry;
    protected final Logger logger;

    private final Function<CustomPacketPayload, Packet<?>> packetFactory;
    private final Set<Identifier> sendableChannels = Collections.synchronizedSet(new HashSet<>());

    private final BlockableEventLoop<?> eventLoop;
    private final Connection connection;

    private boolean initialized = false;

    protected AbstractPacketHandler(String desc, ChannelRegistry<B, C> registry, Function<CustomPacketPayload, Packet<?>> packetFactory, BlockableEventLoop<?> eventLoop, Connection connection) {
        this.logger = LogManager.getLogger(desc);
        this.registry = registry;
        this.packetFactory = packetFactory;
        this.eventLoop = eventLoop;
        this.connection = connection;

        registry.addHandler(this);
    }

    private void receiveChannelSyncPacket(FriendlyByteBuf buf) {
        switch (buf.readByte()) {
            case Constants.CHANNEL_SYNC_SINGLE -> sendableChannels.add(buf.readIdentifier());
            case Constants.CHANNEL_SYNC_INITIAL -> {
                int groupSize = buf.readVarInt();
                for (int i = 0; i < groupSize; i++) {
                    String namespace = buf.readUtf();

                    int pathSize = buf.readVarInt();
                    for (int j = 0; j < pathSize; j++) {
                        String path = buf.readUtf();
                        sendableChannels.add(Identifier.fromNamespaceAndPath(namespace, path));
                    }
                }

                eventLoop.execute(this::onInitialChannelSyncPacketReceived);
            }
        }
    }

    public boolean receive(CustomPacketPayload payload) {
        Identifier id = payload.type().id();

        if (id.equals(Constants.CHANNEL_SYNC)) {
            UntypedPayload untyped = (UntypedPayload) payload;
            receiveChannelSyncPacket(untyped.buffer());
            return true;
        }

        if (registry.has(id)) {
            try {
                PacketReceiver<C, CustomPacketPayload> receiver = registry.get(id);

                if (payload instanceof UntypedPayload || eventLoop.isSameThread()) {
                    receiveUnsafe(receiver, payload);
                } else eventLoop.execute(() -> {
                    if (connection.isConnected()) receiveUnsafe(receiver, payload);
                });
            } catch (Throwable t) {
                logger.error("Error when receiving packet {}", id, t);
                throw t;
            }
            return true;
        }

        return false;
    }

    protected abstract void onInitialChannelSyncPacketReceived();

    protected abstract void receiveUnsafe(PacketReceiver<C, CustomPacketPayload> receiver, CustomPacketPayload payload);

    public void sendInitialChannelSyncPacket() {
        if (!initialized) {
            initialized = true;
            sendVanillaChannelRegisterPacket(Set.of(Constants.CHANNEL_SYNC));

            Set<Identifier> channels = registry.getChannels();
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeByte(Constants.CHANNEL_SYNC_INITIAL);

            Map<String, List<Identifier>> group = channels.stream().collect(Collectors.groupingBy(Identifier::getNamespace));
            buf.writeVarInt(group.size());

            for (Map.Entry<String, List<Identifier>> entry : group.entrySet()) {
                buf.writeUtf(entry.getKey());
                buf.writeVarInt(entry.getValue().size());

                for (Identifier value : entry.getValue()) {
                    buf.writeUtf(value.getPath());
                }
            }

            send(Constants.CHANNEL_SYNC, buf);
            sendVanillaChannelRegisterPacket(channels);
        }
    }

    public void onRegister(Identifier id) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeByte(Constants.CHANNEL_SYNC_SINGLE);
        buf.writeIdentifier(id);
        send(Constants.CHANNEL_SYNC, buf);
        sendVanillaChannelRegisterPacket(Set.of(id));
    }

    public void remove() {
        registry.removeHandler(this);
    }

    private void sendVanillaChannelRegisterPacket(Set<Identifier> channels) {
        if (PlatformProxy.INSTANCE.canSendVanillaRegisterPackets() && !channels.isEmpty()) {
            connection.send(createVanillaRegisterPacket(channels, buf -> {
                boolean first = true;
                for (Identifier channel : channels) {
                    if (first) {
                        first = false;
                    } else {
                        buf.writeByte(0);
                    }
                    buf.writeBytes(channel.toString().getBytes(StandardCharsets.US_ASCII));
                }
            }));
        }
    }

    protected abstract Packet<?> createVanillaRegisterPacket(Set<Identifier> channels, Consumer<B> buf);

    @Override
    public void send(CustomPacketPayload payload, @Nullable ChannelFutureListener callback) {
        connection.send(packetFactory.apply(payload), callback);
    }

    @Override
    public boolean canSend(Identifier id) {
        return sendableChannels.contains(id);
    }

}
