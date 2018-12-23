package mcp.mobius.waila.mixin.client;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "getTooltipText", at = @At("TAIL"))
    private void appendModName(PlayerEntity player, TooltipOptions options, CallbackInfoReturnable<List<TextComponent>> callbackInfo) {
        List<TextComponent> components = callbackInfo.getReturnValue();
        ItemStack stack = (ItemStack) (Object) this;
        components.add(new StringTextComponent(String.format(Waila.config.getFormatting().getModName(), ModIdentification.getModInfo(stack.getItem()).getName())));
    }
}
