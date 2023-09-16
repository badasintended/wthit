package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IMixedService;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientConfigurationPacketListenerImpl.class)
public class ClientConfigurationPacketListenerImplMixin extends ClientCommonPacketListenerImplMixin {

    @Shadow
    private RegistryAccess.Frozen receivedRegistries;

    @Override
    protected void wthit_onHandleUpdateTags() {
        IMixedService.INSTANCE.attachRegistryFilter(receivedRegistries);
    }

}
