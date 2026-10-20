package mcp.mobius.waila.mixin;

import java.util.List;

import mcp.mobius.waila.mixed.IMixinService;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

    @Unique
    @SuppressWarnings("NotNullFieldNotInitialized")
    private RegistryAccess wthit_registryAccess;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void wthit_init(ReloadableServerRegistries.LoadResult loadResult, FeatureFlagSet enabledFeatures, Commands.CommandSelection commandSelection, List<?> postponedTags, PermissionSet functionCompilationPermissions, List<?> newComponents, CallbackInfo ci) {
        wthit_registryAccess = loadResult.layers().compositeAccess();
    }

    @Inject(method = "updateComponentsAndStaticRegistryTags", at = @At("TAIL"))
    private void wthit_onUpdateRegistryTags(CallbackInfo ci) {
        IMixinService.INSTANCE.attachRegistryFilter(wthit_registryAccess);
    }

}
