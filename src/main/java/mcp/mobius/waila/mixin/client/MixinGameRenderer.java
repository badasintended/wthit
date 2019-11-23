package mcp.mobius.waila.mixin.client;

import mcp.mobius.waila.overlay.WailaTickHandler;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "render", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;render(F)V")), at = @At(value = "INVOKE", ordinal = 0))
    private void renderOverlay(float var1, long nanoTime, boolean var4, CallbackInfo callbackInfo) {
        WailaTickHandler.INSTANCE.renderOverlay();
    }
}
