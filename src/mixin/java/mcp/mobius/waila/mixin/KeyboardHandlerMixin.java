package mcp.mobius.waila.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import mcp.mobius.waila.mixed.IClientMixinService;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void wthit_onKeyPress(long l, int action, KeyEvent event, CallbackInfo ci) {
        if (action != InputConstants.PRESS && action != InputConstants.RELEASE) return;

        var key = InputConstants.getKey(event);
        IClientMixinService.INSTANCE.onButton(key, action == InputConstants.PRESS);
    }

}
