package mcp.mobius.waila.mixin.client;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "getTooltip", at = @At("TAIL"))
    private void appendModName(PlayerEntity player, TooltipContext options, CallbackInfoReturnable<List<Component>> callbackInfo) {
        List<Component> components = callbackInfo.getReturnValue();
        ItemStack stack = (ItemStack) (Object) this;
        components.add(new TextComponent(String.format(Waila.CONFIG.get().getFormatting().getModName(), ModIdentification.getModInfo(stack.getItem()).getName())));
    }
}
