package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IMixinService;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConfigurationPacketListenerImpl.class)
public class ClientConfigurationPacketListenerImplMixin {

    @Shadow
    private RegistryAccess.Frozen receivedRegistries;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void wthit_onServerLogin(CallbackInfo ci) {
        IMixinService.INSTANCE.onServerLogin();
    }

    @Inject(method = "handleUpdateTags", at = @At("TAIL"))
    private void wthit_onHandleUpdateTags(ClientboundUpdateTagsPacket $$0, CallbackInfo ci) {
        IMixinService.INSTANCE.attachRegistryFilter(receivedRegistries);
    }

}
