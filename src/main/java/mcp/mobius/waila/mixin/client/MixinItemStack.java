package mcp.mobius.waila.mixin.client;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "getTooltip", at = @At("TAIL"))
    private void appendModName(PlayerEntity player, TooltipContext options, CallbackInfoReturnable<List<Text>> callbackInfo) {
        List<Text> components = callbackInfo.getReturnValue();
        ItemStack stack = (ItemStack) (Object) this;
        components.add(new LiteralText(String.format(Waila.CONFIG.get().getFormatting().getModName(), ModIdentification.getModInfo(stack.getItem()).getName())));
    }
}
