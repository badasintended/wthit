package mcp.mobius.waila.network.common;

import mcp.mobius.waila.Waila;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record VersionPayload(
    int version
) implements CustomPacketPayload {

    public static final ResourceLocation ID = Waila.id("version");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(version);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

}
