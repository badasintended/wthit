package mcp.mobius.waila.forge;

import mcp.mobius.waila.service.ClientApiService;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public class ForgeClientApiService extends ClientApiService {

    @Override
    public ScreenRectangle peekScissorStack(GuiGraphics ctx) {
        return ctx.getScissorStack().peek();
    }

}
