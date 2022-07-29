package mcp.mobius.waila.integration.jei;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.gui.screen.AutoClosableScreen;
import mcp.mobius.waila.integration.IRecipeAction;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JeiRecipeAction implements IModPlugin, IRecipeAction {

    private static final ResourceLocation ID = Waila.id("jei");

    IJeiRuntime jei;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jei) {
        this.jei = jei;
        WailaClient.setRecipeAction(this);
    }

    @Override
    public String getModName() {
        return IModInfo.get("rei_plugin_compatibilities").isPresent()
            ? "REI Plugin Compatibilities"
            : "Just Enough Items";
    }

    @Override
    public void showInput(ItemStack stack) {
        showRecipesGui(RecipeIngredientRole.INPUT, stack);
    }

    @Override
    public void showOutput(ItemStack stack) {
        showRecipesGui(RecipeIngredientRole.OUTPUT, stack);
    }

    void showRecipesGui(RecipeIngredientRole role, ItemStack stack) {
        if (!stack.isEmpty()) {
            AutoClosableScreen.inject();
            jei.getRecipesGui().show(jei.getJeiHelpers().getFocusFactory().createFocus(role, VanillaTypes.ITEM_STACK, stack));
        }
    }

}
