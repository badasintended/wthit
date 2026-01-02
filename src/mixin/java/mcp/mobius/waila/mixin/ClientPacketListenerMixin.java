package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IMixinService;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin extends ClientCommonPacketListenerImplMixin {

    @Shadow
    @Final
    private RegistryAccess.Frozen registryAccess;

    @Override
    protected void wthit_onHandleUpdateTags() {
        IMixinService.INSTANCE.ClientPacketListener_handleUpdateTags(registryAccess);
    }

}
