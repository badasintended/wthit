package mcp.mobius.waila.network;

import mcp.mobius.waila.config.PluginConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class PacketSender {

    public void initMain() {
    }

    public abstract void sendConfig(PluginConfig config, ServerPlayerEntity player);

    @Environment(EnvType.CLIENT)
    public void initClient() {
    }

    @Environment(EnvType.CLIENT)
    public abstract void requestEntity(Entity entity);

    @Environment(EnvType.CLIENT)
    public abstract void requestBlock(BlockEntity blockEntity);

}
