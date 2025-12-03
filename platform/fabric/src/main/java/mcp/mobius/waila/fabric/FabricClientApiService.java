package mcp.mobius.waila.fabric;

import mcp.mobius.waila.service.ClientApiService;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jspecify.annotations.Nullable;

public class FabricClientApiService extends ClientApiService {

    @Override
    public @Nullable ScreenRectangle peekScissorStack(GuiGraphics ctx) {
        return ctx.scissorStack.peek();
    }

}
