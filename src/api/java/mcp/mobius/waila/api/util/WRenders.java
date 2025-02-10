package mcp.mobius.waila.api.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

@ApiSide.ClientOnly
public final class WRenders {

    public static MultiBufferSource bufferSource(GuiGraphics ctx) {
        return IClientApiService.INSTANCE.getBufferSource(ctx);
    }

    public static VertexConsumer buffer(GuiGraphics ctx, RenderType type) {
        return bufferSource(ctx).getBuffer(type);
    }

    private WRenders() {
    }

}
