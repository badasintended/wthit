package mcp.mobius.waila.handlers;

import mezz.jei.api.*;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.gui.Focus;
import mezz.jei.gui.ItemListOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

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
        if (Minecraft.getMinecraft().currentScreen != runtime.getRecipesGui()) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new TextComponentTranslation("client.msg.norecipe").setStyle(new Style().setColor(TextFormatting.RED)));
            return;
        }
        openItemList();
    }

    public static void displayUses(ItemStack stack) {
        runtime.getRecipesGui().show(new Focus<ItemStack>(IFocus.Mode.INPUT, stack));
        if (Minecraft.getMinecraft().currentScreen != runtime.getRecipesGui()) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new TextComponentTranslation("client.msg.nousage").setStyle(new Style().setColor(TextFormatting.RED)));
            return;
        }
        openItemList();
    }

    private static void openItemList() {
        ItemListOverlay itemList = (ItemListOverlay) runtime.getItemListOverlay();
        itemList.open();
    }
}
