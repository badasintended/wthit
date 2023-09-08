package mcp.mobius.waila.mixin;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public class ClientCommonPacketListenerImplMixin {

    @Inject(method = "handleUpdateTags", at = @At("TAIL"))
    private void wthit_onHandleUpdateTags(ClientboundUpdateTagsPacket $$0, CallbackInfo ci) {
        wthit_onHandleUpdateTags();
    }

    @Unique
    protected void wthit_onHandleUpdateTags() {
    }

}
