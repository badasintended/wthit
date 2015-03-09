package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;

public class TTRenderStack implements IWailaTooltipRenderer{

	@Override
	public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
		return new Dimension(16,16);
	}

	@Override
	public void draw(String[] params, IWailaCommonAccessor accessor) {
		int type    = Integer.valueOf(params[0]); //0 for block, 1 for item
		String name = params[1]; //Fully qualified name
		int amount  = Integer.valueOf(params[2]);
		int meta    = Integer.valueOf(params[3]);
		
		ItemStack stack = null;
		if (type == 0)
			stack = new ItemStack((Block)Block.blockRegistry.getObject(name), amount, meta);
		if (type == 1)
			stack = new ItemStack((Item)Item.itemRegistry.getObject(name), amount, meta);
		
		RenderHelper.enableGUIStandardItemLighting();
		DisplayUtil.renderStack(0, 0, stack);
		RenderHelper.disableStandardItemLighting();
	}

}
