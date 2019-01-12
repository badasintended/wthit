package mcp.mobius.waila.mixin.client;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.overlay.WailaTickHandler;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "tick", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/DisableableProfiler;push(Ljava/lang/String;)V")), at = @At(value = "HEAD", ordinal = 0))
    private void clientTick(CallbackInfo callbackInfo) {
        WailaTickHandler.INSTANCE.tickClient();
    }

    @Inject(method = "method_1508", at = @At("TAIL"))
    private void handleKeybinds(CallbackInfo callbackInfo) {
        WailaClient.handleKeybinds();
    }
}
