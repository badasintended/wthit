package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IMixedService;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

    @Inject(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;)V", at = @At("TAIL"))
    private void wthit_onUpdateRegistryTags(RegistryAccess registryAccess, CallbackInfo ci) {
        IMixedService.INSTANCE.attachRegistryFilter(registryAccess);
    }

}
