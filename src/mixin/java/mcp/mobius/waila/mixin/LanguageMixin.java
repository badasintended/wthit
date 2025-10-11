package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IMixinService;
import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Language.class)
public class LanguageMixin {

    @Inject(method = "inject", at = @At("RETURN"))
    private static void wthit_inject(CallbackInfo ci) {
        IMixinService.INSTANCE.onLanguageReloaded();
    }

}
