package mcp.mobius.waila.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import mcp.mobius.waila.mixed.IClientMixinService;
import net.minecraft.client.KeyboardHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void wthit_onKeyPress(long window, int keyCode, int scanCode, int action, int modifier, CallbackInfo ci) {
        if (action != GLFW.GLFW_PRESS && action != GLFW.GLFW_RELEASE) return;

        var key = InputConstants.getKey(keyCode, scanCode);
        IClientMixinService.INSTANCE.onButton(key, action == GLFW.GLFW_PRESS);
    }

}
