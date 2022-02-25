package mcp.mobius.waila.network;

import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public abstract class PacketSender {

    public static final int NETWORK_VERSION = 2;

    public void initMain() {
    }

    public void initClient() {
    }

    public abstract void sendPluginConfig(PluginConfig config, ServerPlayer player);

    public abstract void sendBlacklistConfig(BlacklistConfig config, ServerPlayer player);

    public abstract void generateClientDump(ServerPlayer player);

    public abstract boolean isServerAvailable();

    public abstract void requestEntity(EntityHitResult hitResult);

    public abstract void requestBlock(BlockHitResult hitResult);

}
