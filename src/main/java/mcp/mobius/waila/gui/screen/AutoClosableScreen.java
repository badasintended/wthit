package mcp.mobius.waila.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("DataFlowIssue")
public class AutoClosableScreen extends AbstractContainerScreen<AutoClosableScreen.Menu> {

    private AutoClosableScreen() {
        super(new Menu(), Minecraft.getInstance().player.getInventory(), Component.empty());
    }

    public static void inject() {
        var client = Minecraft.getInstance();
        var screen = client.gui.screen();
        if (!(screen instanceof AutoClosableScreen)) {
            client.gui.setScreen(new AutoClosableScreen());
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (minecraft.gui.screen() == this) {
            minecraft.gui.setScreen(null);
        }
    }

    public static class Menu extends AbstractContainerMenu {

        protected Menu() {
            super(null, 0);
        }

        @Override
        public ItemStack quickMoveStack(Player player, int i) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player) {
            return false;
        }

    }

}
