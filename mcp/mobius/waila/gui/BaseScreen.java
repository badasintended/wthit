package mcp.mobius.waila.gui;

import java.util.HashMap;

import org.lwjgl.input.Mouse;

import mcp.mobius.waila.gui.widgets.IWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

//public abstract class BaseScreen extends GuiScreen {
public class BaseScreen extends GuiScreen {	
	protected GuiScreen parent;					// Return screen if available.
	protected Minecraft mc;						// Minecraft instance
    protected HashMap<String, IWidget> widgets;	// List of widgets on this ui
	
	public BaseScreen(GuiScreen parent) {
		this.parent  = parent;
		this.mc      = Minecraft.getMinecraft();
		this.widgets = new HashMap<String, IWidget>();
		
        Mouse.getDWheel();			// We init the DWheel method (getDWheel returns the value since the last call, so we have to call it once on ui creation)
	}
	
	@Override
	public void drawScreen(int i, int j, float f) {
		super.drawScreen(i, j, f);
	}

	@Override
	public void keyTyped(char keyChar, int keyID)
	{
		if (keyID == 1)
			if (this.parent == null){
				this.mc.displayGuiScreen((GuiScreen)null);
				this.mc.setIngameFocus();
			} else
				this.mc.displayGuiScreen(this.parent);
	} 	
}
