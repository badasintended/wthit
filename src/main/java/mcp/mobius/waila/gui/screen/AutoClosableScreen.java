package mcp.mobius.waila.gui.screen;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("ConstantConditions")
public class AutoClosableScreen extends AbstractContainerScreen<AutoClosableScreen.Menu> {

    private AutoClosableScreen() {
        super(new Menu(), Minecraft.getInstance().player.getInventory(), Component.empty());
    }

    public static void inject() {
        var client = Minecraft.getInstance();
        var screen = client.screen;
        if (!(screen instanceof AutoClosableScreen)) {
            client.setScreen(new AutoClosableScreen());
        }
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrices, float v, int i, int i1) {
        if (minecraft.screen == this) {
            minecraft.setScreen(null);
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
