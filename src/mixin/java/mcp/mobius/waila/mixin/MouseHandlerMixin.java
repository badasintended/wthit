package mcp.mobius.waila.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import mcp.mobius.waila.mixed.IClientMixinService;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onButton", at = @At("HEAD"))
    private void wthit_onButton(long l, MouseButtonInfo info, int action, CallbackInfo ci) {
        var key = InputConstants.Type.MOUSE.getOrCreate(info.button());
        IClientMixinService.INSTANCE.onButton(key, action == GLFW.GLFW_PRESS);
    }

}
