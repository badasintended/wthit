package mcp.mobius.waila.integration;

import net.minecraft.world.item.ItemStack;

public interface IRecipeAction {

    String getModName();

    void showInput(ItemStack stack);

    void showOutput(ItemStack stack);

}
