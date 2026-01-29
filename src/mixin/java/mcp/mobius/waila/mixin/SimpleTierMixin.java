package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IMixinService;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
    "net.neoforged.neoforge.common.SimpleTier",
    "net.minecraftforge.common.ForgeTier",
})
public class SimpleTierMixin {

    @Unique
    private static @Nullable StackWalker wthit_stackWalker;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void wthit_init(CallbackInfo ci) {
        if (wthit_stackWalker == null) wthit_stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

        var caller = wthit_stackWalker.walk(s -> s
            .skip(2)
            .map(StackWalker.StackFrame::getDeclaringClass)
            .findFirst()
            .orElse(null));
        if (caller == null) caller = wthit_stackWalker.getCallerClass();

        IMixinService.INSTANCE.addTierInstance((Tier) this, caller);
    }

}
