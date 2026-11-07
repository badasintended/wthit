package dev.emi.emi.api.stack;

import net.minecraft.world.item.ItemStack;

public abstract class EmiStack implements EmiIngredient {

    public static EmiStack of(ItemStack stack) {
        throw new AssertionError("stub");
    }

}
