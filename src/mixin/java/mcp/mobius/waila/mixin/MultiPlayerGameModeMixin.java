package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.MMultiPlayerGameMode;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin implements MMultiPlayerGameMode {

    @Shadow
    private float destroyProgress;

    @Unique
    boolean wthit_wasDestroyed;

    @Override
    public boolean wthit_wasDestroyed() {
        return wthit_wasDestroyed;
    }

    @Override
    public float wthit_destroyProgress() {
        return destroyProgress;
    }

    @Inject(method = "destroyBlock", at = @At("HEAD"))
    void wthit_onDestroyBlock(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        wthit_wasDestroyed = true;
    }

    @Inject(method = "stopDestroyBlock", at = @At("HEAD"))
    void wthit_onStopDestroyBlock(CallbackInfo ci) {
        wthit_wasDestroyed = false;
    }

}
