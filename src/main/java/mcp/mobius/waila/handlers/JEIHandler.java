package mcp.mobius.waila.handlers;

import mezz.jei.api.*;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.gui.Focus;
import mezz.jei.gui.ItemListOverlay;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@JEIPlugin
public class JEIHandler extends BlankModPlugin {

    private static IJeiRuntime runtime;

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    public static void displayRecipes(ItemStack stack) {
        runtime.getRecipesGui().show(new Focus<ItemStack>(IFocus.Mode.OUTPUT, stack));
        openItemList();
    }

    public static void displayUses(ItemStack stack) {
        runtime.getRecipesGui().show(new Focus<ItemStack>(IFocus.Mode.INPUT, stack));
        openItemList();
    }

    private static void openItemList() {
        ItemListOverlay itemList = (ItemListOverlay) runtime.getItemListOverlay();
        itemList.open();
    }
}
