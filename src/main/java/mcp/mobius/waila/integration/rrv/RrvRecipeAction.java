package mcp.mobius.waila.integration.rrv;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.integration.IRecipeAction;
import net.minecraft.world.item.ItemStack;

public class RrvRecipeAction implements ReliableRecipeViewerClientPlugin, IRecipeAction {

    @Override
    public void onIntegrationInitialize() {
        WailaClient.setRecipeAction(this);
    }

    @Override
    public String getModName() {
        return "Reliable Recipe Viewer";
    }

    @Override
    public void showInput(ItemStack stack) {
        ItemView.openForStackIngredient(stack);
    }

    @Override
    public void showOutput(ItemStack stack) {
        ItemView.openForStackResult(stack, ReliableClientRecipeType.NONE);
    }

}
