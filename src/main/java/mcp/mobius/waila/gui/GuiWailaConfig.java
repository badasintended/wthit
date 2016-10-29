package mcp.mobius.waila.gui;

import mcp.mobius.waila.gui.screens.config.ScreenConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class GuiWailaConfig extends GuiConfig{

    public GuiWailaConfig(GuiScreen parentScreen) {
        super(parentScreen, new ArrayList<IConfigElement>(), "Waila", "Waila", false, false, "Waila");
    }

    @Override
    public void initGui() {
        Minecraft.getMinecraft().displayGuiScreen(new ScreenConfig(parentScreen));
    }
}
