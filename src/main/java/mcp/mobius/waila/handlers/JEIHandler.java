package mcp.mobius.waila.handlers;

import mcp.mobius.waila.utils.ModIdentification;
//import mezz.jei.api.*;
//import mezz.jei.api.recipe.IFocus;
//import mezz.jei.gui.ItemListOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.ModContainer;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

//@JEIPlugin
public class JEIHandler/* extends BlankModPlugin */{

    private static final ModContainer JEI_CONTAINER = ModIdentification.findModContainer("JEI");
    private static final int NO_REFLECT_BUILD = 359;
//    private static IJeiRuntime runtime;

//    @Override
//    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
//        runtime = jeiRuntime;
//    }

    public static void displayRecipes(ItemStack stack) {
//        runtime.getRecipesGui().show(runtime.getRecipeRegistry().createFocus(IFocus.Mode.OUTPUT, stack));
//        if (Minecraft.getMinecraft().currentScreen != runtime.getRecipesGui()) {
//            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new TextComponentTranslation("client.msg.norecipe").setStyle(new Style().setColor(TextFormatting.RED)));
//            return;
//        }
        openItemList();
    }

    public static void displayUses(ItemStack stack) {
//        runtime.getRecipesGui().show(runtime.getRecipeRegistry().createFocus(IFocus.Mode.INPUT, stack));
//        if (Minecraft.getMinecraft().currentScreen != runtime.getRecipesGui()) {
//            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new TextComponentTranslation("client.msg.nousage").setStyle(new Style().setColor(TextFormatting.RED)));
//            return;
//        }
        openItemList();
    }

    private static void openItemList() {
//        if (Integer.parseInt(JEI_CONTAINER.getVersion().split("\\.")[3]) >= NO_REFLECT_BUILD)
//            return;
//
//        try {
//            ItemListOverlay itemList = (ItemListOverlay) runtime.getItemListOverlay();
//            Method open = itemList.getClass().getDeclaredMethod("open");
//            open.invoke(itemList);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
