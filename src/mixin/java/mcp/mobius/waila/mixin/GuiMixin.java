package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IMixinService;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void wthit_render(GuiGraphics ctx, DeltaTracker delta, CallbackInfo ci) {
        IMixinService.INSTANCE.onGuiRender(ctx, delta);
    }

}
