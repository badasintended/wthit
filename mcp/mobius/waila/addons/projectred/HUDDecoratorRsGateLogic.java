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

	/*
	static byte[][] IOARRAY={
		{0,0,0,0}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0},
		{0,0,0,0}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0},
		{0,0,0,0}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0},
		{0,0,0,0}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0},
		{0,0,0,0}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0},
		{0,0,0,0}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0},
		{0,0,0,0}, {0,0,0,0}, {0,0,0,0}
		};
	*/
	
	static String[] IONAMES={ "", "IN", "OUT", "IN_1", "OUT_1", "IN_2", "OUT_2"};
	
	@Override
	public void decorateBlock(ItemStack itemStack, IWailaDataAccessor accessor,	IWailaConfigHandler config) {

		byte[][] IOARRAY={
				{1,1,2,1} /* OR    */, {1,1,2,1} /* NOR   */, {1,2,2,2} /* NOT    */, {1,1,2,1} /* AND   */,
				{1,1,2,1} /* NAND  */, {0,1,2,1} /* XOR   */, {2,1,2,1} /* XNOR   */, {1,2,2,2} /* Buf   */,
				{0,0,0,0} /* Multi */, {1,0,2,0} /* Pulse */, {1,0,2,0} /* Repeat */, {1,2,2,2} /* Rand  */,
				{0,0,0,0} /* Latch */, {0,0,0,0} /* Toggle*/, {1,1,2,2} /* Transp */, {2,0,0,0} /* Light */,
				{0,0,0,0} /* Rain  */, {0,0,0,0} /* Timer */, {0,0,0,0} /* Sequen */, {0,0,0,0} /* Count */,
				{0,0,0,0} /* State */, {0,0,0,0} /* Sync  */, {0,0,0,0} /* Bus    */, {0,0,0,0} /* Null  */,
				{0,0,0,0} /* Inver */, {0,0,0,0} /* Buff  */, {0,0,0,0} /* Cmp    */, {0,0,0,0} /* And   */,
				};		
		
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

		String[] IOStr    = new String[4];
		for (int i = 0; i < 4; i++)
			IOStr[i] = IONAMES[IOARRAY[subID][i]];
		
		String[] IOStrRot = new String[4];
		
		for (int i = 0; i < 4 ; i++){
			int j = i + orient;
			if (j >= 4) j -= 4;
			IOStrRot[j] = IOStr[i];
		}
		
		Tessellator tessellator = Tessellator.instance;
		
		//UIHelper.drawBillboardText(itemStack.getDisplayName(), accessor.getRenderingPosition(), 0.5F, 1.5F, 0.5F, accessor.getPartialFrame());
		
		UIHelper.drawFloatingText(IOStrRot[0],  accessor.getRenderingPosition(),    0.5F, 0.2F,  1.2F, 90F, -180F);  // Orient 0
		UIHelper.drawFloatingText(IOStrRot[1],  accessor.getRenderingPosition(), -0.2F, 0.2F,  0.5F,   90F,   90F);  // Orient 1
		UIHelper.drawFloatingText(IOStrRot[2],  accessor.getRenderingPosition(),  0.5F, 0.2F, -0.2F,   90F,    0F);  // Orient 2
		UIHelper.drawFloatingText(IOStrRot[3],  accessor.getRenderingPosition(),  1.2F, 0.2F,  0.5F,   90F,  -90F);  // Orient 3
		
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
