package mcp.mobius.waila.forge;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.hud.TooltipRenderer;
import mcp.mobius.waila.util.CommonUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class ForgeWailaJei implements IModPlugin {

    private static final ResourceLocation ID = CommonUtil.id("jei");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jei) {
        IRecipesGui gui = jei.getRecipesGui();
        IRecipeManager manager = jei.getRecipeManager();

        WailaClient.onShowRecipeInput = () ->
            gui.show(manager.createFocus(IFocus.Mode.INPUT, TooltipRenderer.getStack()));

        WailaClient.onShowRecipeOutput = () ->
            gui.show(manager.createFocus(IFocus.Mode.OUTPUT, TooltipRenderer.getStack()));
    }

}
