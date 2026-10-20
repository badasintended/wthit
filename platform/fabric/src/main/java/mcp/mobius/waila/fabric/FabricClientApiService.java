package mcp.mobius.waila.fabric;

import java.lang.reflect.Field;

import mcp.mobius.waila.mixed.MScissorStack;
import mcp.mobius.waila.service.ClientApiService;
import mcp.mobius.waila.util.Log;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jspecify.annotations.Nullable;

public class FabricClientApiService extends ClientApiService {

    private static final Log LOG = Log.create();

    private static final @Nullable Field SCISSOR_STACK;

    static {
        Field scissorStack = null;

        try {
            // the field is private and its type is package private since 26.3, so it has to be looked up reflectively
            scissorStack = GuiGraphicsExtractor.class.getDeclaredField("scissorStack");
            scissorStack.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            LOG.error("Failed to resolve the gui scissor stack", e);
        }

        SCISSOR_STACK = scissorStack;
    }

    @Override
    public @Nullable ScreenRectangle peekScissorStack(GuiGraphicsExtractor ctx) {
        if (SCISSOR_STACK == null) {
            return null;
        }

        try {
            return ((MScissorStack) SCISSOR_STACK.get(ctx)).wthit_peek();
        } catch (ReflectiveOperationException e) {
            LOG.error("Failed to peek the gui scissor stack", e);
            return null;
        }
    }

}
