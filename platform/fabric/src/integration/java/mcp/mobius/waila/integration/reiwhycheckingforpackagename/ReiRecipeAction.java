package mcp.mobius.waila.integration.reiwhycheckingforpackagename;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.gui.screen.AutoClosableScreen;
import mcp.mobius.waila.integration.IRecipeAction;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import net.minecraft.world.item.ItemStack;

public class ReiRecipeAction implements REIClientPlugin, IRecipeAction {

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        WailaClient.setRecipeAction(this);
    }

    @Override
    public double getPriority() {
        return Double.MIN_VALUE;
    }

    @Override
    public String getModName() {
        return "Roughly Enough Items";
    }

    @Override
    public void showInput(ItemStack stack) {
        AutoClosableScreen.inject();
        ViewSearchBuilder.builder()
            .addUsagesFor(EntryStack.of(VanillaEntryTypes.ITEM, stack))
            .open();
    }

    @Override
    public void showOutput(ItemStack stack) {
        AutoClosableScreen.inject();
        ViewSearchBuilder.builder()
            .addRecipesFor(EntryStack.of(VanillaEntryTypes.ITEM, stack))
            .open();
    }

}
