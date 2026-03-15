package mcp.mobius.waila.fabric;

import mcp.mobius.waila.service.ClientApiService;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jspecify.annotations.Nullable;

public class FabricClientApiService extends ClientApiService {

    @Override
    public @Nullable ScreenRectangle peekScissorStack(GuiGraphicsExtractor ctx) {
        return ctx.scissorStack.peek();
    }

}
