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
        WailaClient.onShowRecipeInput = () -> showRecipesGui(jei, IFocus.Mode.INPUT);
        WailaClient.onShowRecipeOutput = () -> showRecipesGui(jei, IFocus.Mode.OUTPUT);
    }

    private void showRecipesGui(IJeiRuntime jei, IFocus.Mode mode) {
        ItemStack stack = DataAccessor.INSTANCE.getStack();
        if (!stack.isEmpty()) {
            jei.getRecipesGui().show(jei.getRecipeManager().createFocus(mode, stack));
        }
    }

}
