package mcp.mobius.waila.addons.projectred;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.gui.helpers.UIHelper;

public class HUDDecoratorRsGateLogic implements IWailaBlockDecorator {

	@Override
	public void decorateBlock(ItemStack itemStack, IWailaDataAccessor accessor,	IWailaConfigHandler config) {

		int orient = 0;
		int subID  = 0;
		int shape  = 0;
		boolean found = false;
		
		if (!accessor.getNBTData().hasKey("parts")) return;
		NBTTagList parts = accessor.getNBTData().getTagList("parts"); 
		for (int i = 0; i < parts.tagCount(); i++){
			NBTTagCompound subtag = (NBTTagCompound)parts.tagAt(i);
			String id = subtag.getString("id");
			
			if (id.equals("pr_sgate")){
				orient = accessor.getNBTInteger(subtag, "orient");
				subID  = accessor.getNBTInteger(subtag, "subID");
				shape  = accessor.getNBTInteger(subtag, "shape");
				found  = true;
			}
		}

		if (!found) return;
		
		
		
		Tessellator tessellator = Tessellator.instance;
		UIHelper.drawFloatingText("IN",  accessor.getRenderingPosition(),  0.5F, 0.2F, -0.2F, 90F,   0F);
		
		//UIHelper.drawBillboardText(itemStack.getDisplayName(), accessor.getRenderingPosition(), 0.5F, 1.5F, 0.5F, accessor.getPartialFrame());
		//UIHelper.drawFloatingText("OUT", accessor.getRenderingPosition(), -0.2F, 0.2F,  0.5F, 90F,  90F);
		//UIHelper.drawFloatingText("OUT", accessor.getRenderingPosition(),  1.2F, 0.2F,  0.5F, 90F, -90F);
		//UIHelper.drawFloatingText("OUT", accessor.getRenderingPosition(),  0.5F, 0.2F,  1.2F, 90F, -180F);
      
        double offset = 0.1;
        double delta = 1 + 2 * offset;
        
        double x = accessor.getRenderingPosition().xCoord - offset;
        double y = accessor.getRenderingPosition().yCoord - offset;
        double z = accessor.getRenderingPosition().zCoord - offset;
		
        tessellator.startDrawingQuads();

        tessellator.setColorRGBA(255, 255, 255, 150);

        tessellator.addVertex(x,          y + 0.2 , z);
        tessellator.addVertex(x,          y + 0.2 , z + delta/2 - 0.1);
        tessellator.addVertex(x + offset, y + 0.2,  z + delta/2 - 0.1);
        tessellator.addVertex(x + offset, y + 0.2,  z);

        tessellator.addVertex(x,          y + 0.2 , z + delta/2 + 0.1);
        tessellator.addVertex(x,          y + 0.2 , z + delta);
        tessellator.addVertex(x + offset, y + 0.2,  z + delta);
        tessellator.addVertex(x + offset, y + 0.2,  z + delta/2 + 0.1);        

        tessellator.addVertex(x + delta - 0.1,          y + 0.2 , z + 0.1);
        tessellator.addVertex(x + delta - 0.1,          y + 0.2 , z + delta/2 - 0.1);
        tessellator.addVertex(x + delta + offset - 0.1, y + 0.2,  z + delta/2 - 0.1);
        tessellator.addVertex(x + delta + offset - 0.1, y + 0.2,  z + 0.1);

        tessellator.addVertex(x + delta - 0.1,          y + 0.2 , z + delta/2 + 0.1);
        tessellator.addVertex(x + delta - 0.1,          y + 0.2 , z + delta);
        tessellator.addVertex(x + delta + offset - 0.1, y + 0.2,  z + delta);
        tessellator.addVertex(x + delta + offset - 0.1, y + 0.2,  z + delta/2 + 0.1);        

        
        tessellator.addVertex(x+ 0.1,           y + 0.2,  z);
        tessellator.addVertex(x+ 0.1,           y + 0.2,  z+ offset);
        tessellator.addVertex(x+ delta/2 - 0.1, y + 0.2,  z+ offset);
        tessellator.addVertex(x+ delta/2 - 0.1, y + 0.2,  z);

        tessellator.addVertex(x+ delta/2 + 0.1, y + 0.2,  z);
        tessellator.addVertex(x+ delta/2 + 0.1, y + 0.2,  z+ offset);
        tessellator.addVertex(x+ delta,         y + 0.2,  z+ offset);
        tessellator.addVertex(x+ delta,         y + 0.2,  z);        

        tessellator.addVertex(x + 0.1,                y + 0.2,  z+ delta - 0.1);
        tessellator.addVertex(x + 0.1,                y + 0.2,  z+ offset+ delta - 0.1);
        tessellator.addVertex(x+ delta/2 - 0.1, y + 0.2,  z+ offset+ delta - 0.1);
        tessellator.addVertex(x+ delta/2 - 0.1, y + 0.2,  z+ delta - 0.1);

        tessellator.addVertex(x+ delta/2 + 0.1, y + 0.2,  z+ delta - 0.1);
        tessellator.addVertex(x+ delta/2 + 0.1, y + 0.2,  z+ offset+ delta - 0.1);
        tessellator.addVertex(x+ delta - 0.1,   y + 0.2,  z+ offset+ delta - 0.1);
        tessellator.addVertex(x+ delta - 0.1,   y + 0.2,  z+ delta - 0.1);           
        
        tessellator.draw();			
		
	}

}
