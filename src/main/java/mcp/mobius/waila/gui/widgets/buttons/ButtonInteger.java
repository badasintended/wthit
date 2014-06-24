package mcp.mobius.waila.gui.widgets.buttons;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.util.Point;

public class ButtonInteger extends ButtonBase {

	protected int state = 0;
	protected int nStates;
	
	public ButtonInteger(IWidget parent, String... texts){
		super(parent);
		
		this.nStates = texts.length;
		
		for (int i = 0; i < texts.length; i++){
			String labelName = String.format("Label_%d", i); 
			this.addWidget(labelName, new LabelFixedFont(this, texts[i]));
			this.getWidget(labelName).hide();
			this.getWidget(labelName).setGeometry(new WidgetGeometry(50.0D, 50.0D, 100.0D, 20.0D, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
		}
		this.getWidget("Label_0").show();
	}

	@Override
	public void onMouseClick(MouseEvent event){
		super.onMouseClick(event);		
		
		if (event.button == 0)
			this.state += 1;
		
		if (this.state >= this.nStates)
			this.state = 0;
		
		for (int i = 0; i < this.nStates; i++)
			this.getWidget(String.format("Label_%d", i)).hide();
		
		this.getWidget(String.format("Label_%d", state)).show();		
	}

}
