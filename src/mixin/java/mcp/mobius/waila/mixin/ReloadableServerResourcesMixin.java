package mcp.mobius.waila.mixin;

import java.util.List;

import mcp.mobius.waila.mixed.IMixinService;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

    @Unique
    private RegistryAccess wthit_registryAccess;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void wthit_init(LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess, HolderLookup.Provider provider, FeatureFlagSet featureFlagSet, Commands.CommandSelection commandSelection, List<?> list, int i, CallbackInfo ci) {
        wthit_registryAccess = layeredRegistryAccess.compositeAccess();
    }

    @Inject(method = "updateStaticRegistryTags()V", at = @At("TAIL"))
    private void wthit_onUpdateRegistryTags(CallbackInfo ci) {
        IMixinService.INSTANCE.attachRegistryFilter(wthit_registryAccess);
    }

}
