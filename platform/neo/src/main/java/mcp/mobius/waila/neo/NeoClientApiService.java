package mcp.mobius.waila.neo;

import mcp.mobius.waila.service.ClientApiService;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jspecify.annotations.Nullable;

public class NeoClientApiService extends ClientApiService {

    @Override
    public @Nullable ScreenRectangle peekScissorStack(GuiGraphicsExtractor ctx) {
        return ctx.peekScissorStack();
    }

}
