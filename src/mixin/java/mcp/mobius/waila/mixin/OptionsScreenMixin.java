package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IClientMixinService;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin {

    @Unique
    private GridLayout.RowHelper wthit_rowHelper;

    @ModifyVariable(method = "init", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/layouts/GridLayout;createRowHelper(I)Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;"))
    private GridLayout.RowHelper wthit_saveRowHelper(GridLayout.RowHelper helper) {
        this.wthit_rowHelper = helper;
        return helper;
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;addToContents(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;"))
    private void wthit_onInit(CallbackInfo ci) {
        IClientMixinService.INSTANCE.optionsScreenRow(this.wthit_rowHelper);
        wthit_rowHelper = null;
    }

}
