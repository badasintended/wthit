package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.overlay.DataAccessor;
import me.shedaniel.rei.api.ClientHelper;
import me.shedaniel.rei.api.ClientHelper.ViewSearchBuilder;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.util.Identifier;

public class FabricWailaRei implements REIPluginV0 {

    private static final Identifier ID = Waila.id("rei");

    @Override
    public Identifier getPluginIdentifier() {
        return ID;
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        WailaClient.onShowRecipeInput = () -> {
            ViewSearchBuilder view = ViewSearchBuilder.builder()
                .addUsagesFor(EntryStack.create(DataAccessor.INSTANCE.getStack()));
            ClientHelper.getInstance().openView(view);
        };

        WailaClient.onShowRecipeOutput = () -> {
            ViewSearchBuilder view = ViewSearchBuilder.builder()
                .addRecipesFor(EntryStack.create(DataAccessor.INSTANCE.getStack()));
            ClientHelper.getInstance().openView(view);
        };
    }

}
