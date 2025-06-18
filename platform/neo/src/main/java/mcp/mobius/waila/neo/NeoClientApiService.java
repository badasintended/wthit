package mcp.mobius.waila.neo;

import mcp.mobius.waila.service.ClientApiService;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public class NeoClientApiService extends ClientApiService {

    @Override
    public ScreenRectangle peekScissorStack(GuiGraphics ctx) {
        return ctx.peekScissorStack();
    }

}
