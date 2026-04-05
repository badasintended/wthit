package mcp.mobius.waila.forge;

import mcp.mobius.waila.mixed.MScissorStack;
import mcp.mobius.waila.service.ClientApiService;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jspecify.annotations.Nullable;

public class ForgeClientApiService extends ClientApiService {

    @Override
    public @Nullable ScreenRectangle peekScissorStack(GuiGraphicsExtractor ctx) {
        return ((MScissorStack) ctx.getScissorStack()).wthit_peek();
    }

}
