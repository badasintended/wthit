package mcp.mobius.waila.network;

import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class NetworkHandler {

    public void main() {
    }

    public abstract void sendConfig(PluginConfig config, ServerPlayerEntity player);

    @Environment(EnvType.CLIENT)
    public void client() {
    }

    @Environment(EnvType.CLIENT)
    public abstract void requestEntity(Entity entity);

    @Environment(EnvType.CLIENT)
    public abstract void requestBlock(BlockEntity blockEntity);

}
