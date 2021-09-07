package mcp.mobius.waila.network;

import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class PacketSender {

    public void initMain() {
    }

    public void initClient() {
    }

    public abstract void sendConfig(PluginConfig config, ServerPlayer player);

    public abstract void generateClientDump(ServerPlayer player);

    public abstract boolean isServerAvailable();

    public abstract void requestEntity(Entity entity);

    public abstract void requestBlock(BlockEntity blockEntity);

}
