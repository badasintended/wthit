package mcp.mobius.waila.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import mcp.mobius.waila.mixed.IClientMixinService;
import net.minecraft.client.MouseHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onPress", at = @At("HEAD"))
    private void wthit_onPress(long window, int button, int action, int mods, CallbackInfo ci) {
        var key = InputConstants.Type.MOUSE.getOrCreate(button);
        IClientMixinService.INSTANCE.onButton(key, action == GLFW.GLFW_PRESS);
    }

}
