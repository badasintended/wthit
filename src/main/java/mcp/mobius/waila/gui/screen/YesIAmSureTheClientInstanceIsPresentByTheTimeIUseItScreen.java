package mcp.mobius.waila.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@SuppressWarnings({"NotNullFieldNotInitialized"})
public abstract class YesIAmSureTheClientInstanceIsPresentByTheTimeIUseItScreen extends Screen {

    Minecraft minecraft;

    public YesIAmSureTheClientInstanceIsPresentByTheTimeIUseItScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft = super.minecraft;
    }

}
