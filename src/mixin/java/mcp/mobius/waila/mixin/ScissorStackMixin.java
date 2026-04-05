package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.MScissorStack;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.client.gui.GuiGraphicsExtractor$ScissorStack")
public abstract class ScissorStackMixin implements MScissorStack {

    @Shadow
    public abstract @Nullable ScreenRectangle peek();

    @Override
    public @Nullable ScreenRectangle wthit_peek() {
        return peek();
    }

}
