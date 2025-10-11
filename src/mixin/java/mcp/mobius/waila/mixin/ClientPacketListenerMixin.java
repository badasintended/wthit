package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IMixinService;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow
    @Final
    private RegistryAccess.Frozen registryAccess;

    @Inject(method = "handleUpdateTags", at = @At("TAIL"))
    private void wthit_onHandleUpdateTags(ClientboundUpdateTagsPacket $$0, CallbackInfo ci) {
        IMixinService.INSTANCE.attachRegistryFilter(registryAccess);
    }

}
