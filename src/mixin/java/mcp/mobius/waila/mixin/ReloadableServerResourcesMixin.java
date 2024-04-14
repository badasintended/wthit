package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IMixedService;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

    @Shadow
    private ReloadableServerRegistries.Holder fullRegistryHolder;

    @Inject(method = "updateRegistryTags()V", at = @At("TAIL"))
    private void wthit_onUpdateRegistryTags(CallbackInfo ci) {
        IMixedService.INSTANCE.attachRegistryFilter(fullRegistryHolder.get());
    }

}
