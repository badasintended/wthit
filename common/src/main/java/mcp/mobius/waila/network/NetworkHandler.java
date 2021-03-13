package mcp.mobius.waila.network;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class NetworkHandler {

    public static final Identifier REQUEST_ENTITY = new Identifier(Waila.MODID, "request_entity");
    public static final Identifier REQUEST_TILE = new Identifier(Waila.MODID, "request_tile");

    @ExpectPlatform
    public static void init() {
    }

    @ExpectPlatform
    @Environment(EnvType.CLIENT)
    public static void requestEntity(Entity entity) {
    }

    @ExpectPlatform
    @Environment(EnvType.CLIENT)
    public static void requestTile(BlockEntity blockEntity) {
    }

    @ExpectPlatform
    public static void sendConfig(PluginConfig config, ServerPlayerEntity player) {
    }

}
