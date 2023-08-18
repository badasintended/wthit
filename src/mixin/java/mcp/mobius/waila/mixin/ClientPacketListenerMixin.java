package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IMixedService;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow
    private LayeredRegistryAccess<ClientRegistryLayer> registryAccess;

    @Inject(method = "handleUpdateTags", at = @At("TAIL"))
    private void wthit_onHandleUpdateTags(ClientboundUpdateTagsPacket packet, CallbackInfo ci) {
        IMixedService.INSTANCE.ClientPacketListener_handleUpdateTags(registryAccess);
    }

}
