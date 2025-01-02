package mcp.mobius.waila.api.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

@ApiSide.ClientOnly
public final class WRenders {

    public static MultiBufferSource bufferSource() {
        return Minecraft.getInstance().renderBuffers().bufferSource();
    }

    public static VertexConsumer buffer(RenderType type) {
        return bufferSource().getBuffer(type);
    }

    private WRenders() {
    }

}
