package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IMixinService;
import net.minecraft.world.item.ToolMaterial;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToolMaterial.class)
public class ToolMaterialMixin {

    @Unique
    private static @Nullable StackWalker wthit_stackWalker;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void wthit_init(CallbackInfo ci) {
        if (wthit_stackWalker == null) wthit_stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        IMixinService.INSTANCE.addToolMaterialInstance((ToolMaterial) (Object) this, wthit_stackWalker.getCallerClass());
    }

}
