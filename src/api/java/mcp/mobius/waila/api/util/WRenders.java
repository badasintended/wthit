package mcp.mobius.waila.api.util;

import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.jspecify.annotations.Nullable;

@ApiSide.ClientOnly
public final class WRenders {

    public static GuiRenderState state(GuiGraphicsExtractor ctx) {
        return IClientApiService.INSTANCE.getRenderState(ctx);
    }

    public static @Nullable ScreenRectangle scissor(GuiGraphicsExtractor ctx) {
        return IClientApiService.INSTANCE.peekScissorStack(ctx);
    }

    private WRenders() {
    }

}
