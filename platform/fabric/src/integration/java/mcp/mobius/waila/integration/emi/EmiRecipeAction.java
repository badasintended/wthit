package mcp.mobius.waila.integration.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.gui.screen.AutoClosableScreen;
import mcp.mobius.waila.integration.IRecipeAction;
import net.minecraft.world.item.ItemStack;

public class EmiRecipeAction implements EmiPlugin, IRecipeAction {

    @Override
    public void register(EmiRegistry registry) {
        WailaClient.setRecipeAction(this);
    }

    @Override
    public String getModName() {
        return "EMI";
    }

    @Override
    public void showInput(ItemStack stack) {
        AutoClosableScreen.inject();
        EmiApi.displayUses(EmiStack.of(stack));
    }

    @Override
    public void showOutput(ItemStack stack) {
        AutoClosableScreen.inject();
        EmiApi.displayRecipes(EmiStack.of(stack));
    }

}
