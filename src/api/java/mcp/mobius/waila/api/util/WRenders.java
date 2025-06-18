package mcp.mobius.waila.api.util;

import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiRenderState;

@ApiSide.ClientOnly
public final class WRenders {

    public static GuiRenderState state(GuiGraphics ctx) {
        return IClientApiService.INSTANCE.getRenderState(ctx);
    }

    public static ScreenRectangle scissor(GuiGraphics ctx) {
        return IClientApiService.INSTANCE.peekScissorStack(ctx);
    }

    private WRenders() {
    }

}
