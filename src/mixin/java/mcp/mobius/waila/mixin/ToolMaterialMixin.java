package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IMixedService;
import net.minecraft.world.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToolMaterial.class)
public class ToolMaterialMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void wthit_init(CallbackInfo ci) {
        IMixedService.INSTANCE.addToolMaterialInstance((ToolMaterial) (Object) this);
    }

}
