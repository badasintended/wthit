package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IClientMixinService;
import mcp.mobius.waila.mixed.IMixinService;
import mcp.mobius.waila.mixed.MWrappedKeyBind;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {

    @Inject(method = "resetMapping", at = @At("HEAD"))
    private static void wthit_resetMapping(CallbackInfo ci) {
        for (var bind : IClientMixinService.INSTANCE.getWrappedBinds()) {
            ((MWrappedKeyBind) bind).update();
        }
        IMixinService.INSTANCE.saveConfig();
    }

}
