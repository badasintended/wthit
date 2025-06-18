package mcp.mobius.waila.fabric;

import mcp.mobius.waila.service.ClientApiService;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public class FabricClientApiService extends ClientApiService {

    @Override
    public ScreenRectangle peekScissorStack(GuiGraphics ctx) {
        return ctx.scissorStack.peek();
    }

}
