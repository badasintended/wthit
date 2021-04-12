package mcp.mobius.waila.forge;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.overlay.DataAccessor;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class ForgeWailaJei implements IModPlugin {

    private static final Identifier ID = Waila.id("jei");

    @Override
    public @NotNull Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jei) {
        IRecipesGui gui = jei.getRecipesGui();
        IRecipeManager manager = jei.getRecipeManager();

        WailaClient.onShowRecipeInput = () ->
            gui.show(manager.createFocus(IFocus.Mode.INPUT, DataAccessor.INSTANCE.getStack()));

        WailaClient.onShowRecipeOutput = () ->
            gui.show(manager.createFocus(IFocus.Mode.OUTPUT, DataAccessor.INSTANCE.getStack()));
    }

}
