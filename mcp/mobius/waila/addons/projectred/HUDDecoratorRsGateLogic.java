package mcp.mobius.waila.addons.projectred;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.gui.helpers.UIHelper;

public class HUDDecoratorRsGateLogic implements IWailaBlockDecorator {

	static byte[][] IOARRAY={
		{1,1,2,1} /* OR    */, {1,1,2,1} /* NOR   */, {1,2,2,2} /* NOT    */, {1,1,2,1} /* AND   */,
		{1,1,2,1} /* NAND  */, {0,1,2,1} /* XOR   */, {0,1,2,1} /* XNOR   */, {1,2,2,2} /* Buf   */,
		{3,4,2,5} /* Multi */, {1,0,2,0} /* Pulse */, {1,0,2,0} /* Repeat */, {1,2,2,2} /* Rand  */,
		{2,1,2,1} /* Latch */, {2,1,2,1} /* Toggle*/, {6,1,2,2} /* Transp */, {2,0,0,0} /* Light */,
		{2,0,0,0} /* Rain  */, {1,7,2,7} /* Timer */, {2,2,2,2} /* Sequen */, {2,9,2,8} /* Count */,
		{1,2,2,6} /* State */, {6,1,2,1} /* Sync  */, {10,1,10,1} /* Bus    */, {11,12,11,12} /* Null  */,
		{11,12,11,12} /* Inver */, {11,12,11,12} /* Buff  */, {1,9,2,8} /* Cmp    */, {1,12,2,12} /* And   */,
		};		
	
	static String[] IONAMES={ "", "IN", "OUT", "SWAP", "IN_A", "IN_B", "LOCK", "IO", "POS", "NEG", "BUS", "A", "B"};
	
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
			
			if (id.equals("pr_sgate") || id.equals("pr_igate") || 
			    id.equals("pr_tgate") || id.equals("pr_bgate") || 
			    id.equals("pr_agate") || id.equals("pr_rgate")){
				orient = accessor.getNBTInteger(subtag, "orient");
				subID  = accessor.getNBTInteger(subtag, "subID");
				shape  = accessor.getNBTInteger(subtag, "shape");
				found  = true;
			}
		}
		
		if (!found) return;

		//orient = orient - ((orient & 0x10) + (orient & 0x8) + (orient & 0x4));
		int hOrient = orient & 0x3;
		ForgeDirection vOrient = ForgeDirection.getOrientation((orient - (orient & 0x3)) >> 2);
		
		//System.out.printf("%s\n", ForgeDirection.getOrientation(vOrient));
		
		String[] IOStr    = new String[4];
		for (int i = 0; i < 4; i++)
			IOStr[i] = IONAMES[IOARRAY[subID][i]];
		
		String[] IOStrRot = new String[4];
		
		for (int i = 0; i < 4 ; i++){
			int j = i + hOrient;
			if (j >= 4) j -= 4;
			IOStrRot[j] = IOStr[i];
		}
		
		switch(vOrient){
		case DOWN:
			UIHelper.drawFloatingText(IOStrRot[0],  accessor.getRenderingPosition(),  0.5F, 0.2F,  1.4F,   90F,    0F, 0F);  // Orient 0
			UIHelper.drawFloatingText(IOStrRot[1],  accessor.getRenderingPosition(), -0.4F, 0.2F,  0.5F,   90F,  270F, 0F);  // Orient 1
			UIHelper.drawFloatingText(IOStrRot[2],  accessor.getRenderingPosition(),  0.5F, 0.2F, -0.4F,   90F,  180F, 0F);  // Orient 2
			UIHelper.drawFloatingText(IOStrRot[3],  accessor.getRenderingPosition(),  1.4F, 0.2F,  0.5F,   90F,   90F, 0F);  // Orient 3				
			break;
		case EAST:
			UIHelper.drawFloatingText(IOStrRot[0],  accessor.getRenderingPosition(),  0.8F,   0.5F,  1.4F, 0F, 90F,   90F);
			UIHelper.drawFloatingText(IOStrRot[1],  accessor.getRenderingPosition(),  0.8F,  -0.4F,  0.5F, 0F, 90F,  180F);
			UIHelper.drawFloatingText(IOStrRot[2],  accessor.getRenderingPosition(),  0.8F,   0.5F, -0.4F, 0F, 90F,  270F);
			UIHelper.drawFloatingText(IOStrRot[3],  accessor.getRenderingPosition(),  0.8F,   1.4F,  0.5F, 0F, 90F,    0F);			
			break;
		case NORTH:
			UIHelper.drawFloatingText(IOStrRot[0],  accessor.getRenderingPosition(),  0.5F,  1.4F, 0.2F, 0F, 180F,   0F);
			UIHelper.drawFloatingText(IOStrRot[1],  accessor.getRenderingPosition(),  1.4F,  0.5F, 0.2F, 0F, 180F,  90F);
			UIHelper.drawFloatingText(IOStrRot[2],  accessor.getRenderingPosition(),  0.5F, -0.4F, 0.2F, 0F, 180F, 180F);
			UIHelper.drawFloatingText(IOStrRot[3],  accessor.getRenderingPosition(), -0.4F,  0.5F, 0.2F, 0F, 180F, 270F);			
			break;
		case SOUTH:
			UIHelper.drawFloatingText(IOStrRot[0],  accessor.getRenderingPosition(),  0.5F,  1.4F, 0.8F, 0F, 0F,   0F);
			UIHelper.drawFloatingText(IOStrRot[1],  accessor.getRenderingPosition(), -0.4F,  0.5F, 0.8F, 0F, 0F,  90F);
			UIHelper.drawFloatingText(IOStrRot[2],  accessor.getRenderingPosition(),  0.5F, -0.4F, 0.8F, 0F, 0F, 180F);
			UIHelper.drawFloatingText(IOStrRot[3],  accessor.getRenderingPosition(),  1.4F,  0.5F, 0.8F, 0F, 0F, 270F);			
			break;
		case UP:
			UIHelper.drawFloatingText(IOStrRot[0],  accessor.getRenderingPosition(),  0.5F, 0.8F,  1.4F, 270F,  180F, 0F);
			UIHelper.drawFloatingText(IOStrRot[1],  accessor.getRenderingPosition(),  1.4F, 0.8F,  0.5F, 270F,  270F, 0F);
			UIHelper.drawFloatingText(IOStrRot[2],  accessor.getRenderingPosition(),  0.5F, 0.8F, -0.4F, 270F,  0F,   0F);
			UIHelper.drawFloatingText(IOStrRot[3],  accessor.getRenderingPosition(), -0.4F, 0.8F,  0.5F, 270F,  90F,  0F);			
			break;
		case WEST:
			UIHelper.drawFloatingText(IOStrRot[0],  accessor.getRenderingPosition(), 0.2F,  0.5F,  1.4F, 180F, 90F,  90F);
			UIHelper.drawFloatingText(IOStrRot[1],  accessor.getRenderingPosition(), 0.2F,  1.4F,  0.5F, 180F, 90F, 180F);
			UIHelper.drawFloatingText(IOStrRot[2],  accessor.getRenderingPosition(), 0.2F,  0.5F, -0.4F, 180F, 90F, 270F);
			UIHelper.drawFloatingText(IOStrRot[3],  accessor.getRenderingPosition(), 0.2F, -0.4F,  0.5F, 180F, 90F,   0F);			
			break;
		default:
			break;
		
		}
		
        double x = accessor.getRenderingPosition().xCoord;
        double y = accessor.getRenderingPosition().yCoord;
        double z = accessor.getRenderingPosition().zCoord;
		
		switch(vOrient){
		case DOWN:
	        UIHelper.drawRectangle(x - 0.1, y + 0.1, z,        x,       y + 0.1, z + 0.35, 255, 255, 255, 150 );
	        UIHelper.drawRectangle(x - 0.1, y + 0.1, z + 0.65, x,       y + 0.1, z + 1.0,  255, 255, 255, 150 );
	        UIHelper.drawRectangle(x + 1.0, y + 0.1, z,        x + 1.1, y + 0.1, z + 0.35, 255, 255, 255, 150 );
	        UIHelper.drawRectangle(x + 1.0, y + 0.1, z + 0.65, x + 1.1, y + 0.1, z + 1.0,  255, 255, 255, 150 );	        

	        UIHelper.drawRectangle(x - 0.1,  y + 0.1, z - 0.1, x + 0.35,  y + 0.1, z + 0.1, 255, 255, 255, 150 );
	        UIHelper.drawRectangle(x + 0.65, y + 0.1, z - 0.1, x + 1.1,   y + 0.1, z + 0.1, 255, 255, 255, 150 );
	        UIHelper.drawRectangle(x - 0.1,  y + 0.1, z + 1.0, x + 0.35,  y + 0.1, z + 1.1, 255, 255, 255, 150 );
	        UIHelper.drawRectangle(x + 0.65, y + 0.1, z + 1.0, x + 1.1,   y + 0.1, z + 1.1, 255, 255, 255, 150 );	        
			break;
		case EAST:
			UIHelper.drawRectangleEW(x + 0.9, y,        z - 0.1, x + 0.9, y + 0.35, z, 255, 255, 255, 150 );
			UIHelper.drawRectangleEW(x + 0.9, y + 0.65, z - 0.1, x + 0.9, y + 1.0,  z, 255, 255, 255, 150 );
			UIHelper.drawRectangleEW(x + 0.9, y,        z + 1.0, x + 0.9, y + 0.35, z + 1.1, 255, 255, 255, 150 );
			UIHelper.drawRectangleEW(x + 0.9, y + 0.65, z + 1.0, x + 0.9, y + 1.0,  z + 1.1, 255, 255, 255, 150 );			

			UIHelper.drawRectangleEW(x + 0.9, y - 0.1, z - 0.1,  x + 0.9, y, z + 0.35, 255, 255, 255, 150 );
			UIHelper.drawRectangleEW(x + 0.9, y - 0.1, z + 0.65, x + 0.9, y, z + 1.1, 255, 255, 255, 150 );			
			UIHelper.drawRectangleEW(x + 0.9, y + 1.0, z - 0.1,  x + 0.9, y + 1.1, z + 0.35, 255, 255, 255, 150 );
			UIHelper.drawRectangleEW(x + 0.9, y + 1.0, z + 0.65, x + 0.9, y + 1.1, z + 1.1, 255, 255, 255, 150 );			
			break;
		case NORTH:
			UIHelper.drawRectangle(x - 0.1, y,        z + 0.1, x,       y + 0.35, z + 0.1, 255, 255, 255, 150 );
			UIHelper.drawRectangle(x - 0.1, y + 0.65, z + 0.1, x,       y + 1.0,  z + 0.1, 255, 255, 255, 150 );
			UIHelper.drawRectangle(x + 1.0, y,        z + 0.1, x + 1.1, y + 0.35, z + 0.1, 255, 255, 255, 150 );
			UIHelper.drawRectangle(x + 1.0, y + 0.65, z + 0.1, x + 1.1, y + 1.0,  z + 0.1, 255, 255, 255, 150 );			

			UIHelper.drawRectangle(x - 0.1,  y - 0.1, z + 0.1, x + 0.35, y,       z + 0.1, 255, 255, 255, 150 );
			UIHelper.drawRectangle(x + 0.65, y - 0.1, z + 0.1, x + 1.1,  y,       z + 0.1, 255, 255, 255, 150 );
			UIHelper.drawRectangle(x - 0.1,  y + 1.0, z + 0.1, x + 0.35, y + 1.1, z + 0.1, 255, 255, 255, 150 );
			UIHelper.drawRectangle(x + 0.65, y + 1.0, z + 0.1, x + 1.1,  y + 1.1, z + 0.1, 255, 255, 255, 150 );
			break;
		case SOUTH:
			UIHelper.drawRectangle(x, y,              z + 0.9, x - 0.1, y + 0.35, z + 0.9, 255, 255, 255, 150 );
			UIHelper.drawRectangle(x, y + 0.65,       z + 0.9, x - 0.1, y + 1.0,  z + 0.9, 255, 255, 255, 150 );
			UIHelper.drawRectangle(x + 1.1, y,        z + 0.9, x + 1.0, y + 0.35, z + 0.9, 255, 255, 255, 150 );
			UIHelper.drawRectangle(x + 1.1, y + 0.65, z + 0.9, x + 1.0, y + 1.0,  z + 0.9, 255, 255, 255, 150 );			

			UIHelper.drawRectangle(x + 0.35,  y - 0.1, z + 0.9, x - 0.1,   y,       z + 0.9, 255, 255, 255, 150 );
			UIHelper.drawRectangle(x + 1.10,  y - 0.1, z + 0.9, x + 0.65,  y,       z + 0.9, 255, 255, 255, 150 );
			UIHelper.drawRectangle(x + 0.35,  y + 1.0, z + 0.9, x - 0.1,   y + 1.1, z + 0.9, 255, 255, 255, 150 );
			UIHelper.drawRectangle(x + 1.1,   y + 1.0, z + 0.9, x + 0.65,  y + 1.1, z + 0.9, 255, 255, 255, 150 );			
			break;
		case UP:
	        UIHelper.drawRectangle(x, y + 0.9, z,        x - 0.1,       y + 0.9, z + 0.35, 255, 255, 255, 150 );
	        UIHelper.drawRectangle(x, y + 0.9, z + 0.65, x - 0.1,       y + 0.9, z + 1.0,  255, 255, 255, 150 );
	        UIHelper.drawRectangle(x + 1.1, y + 0.9, z,        x + 1.0, y + 0.9, z + 0.35, 255, 255, 255, 150 );
	        UIHelper.drawRectangle(x + 1.1, y + 0.9, z + 0.65, x + 1.0, y + 0.9, z + 1.0,  255, 255, 255, 150 );	        

	        UIHelper.drawRectangle(x + 0.35,  y + 0.9, z - 0.1, x - 0.1,  y + 0.9, z + 0.1, 255, 255, 255, 150 );
	        UIHelper.drawRectangle(x + 1.1,   y + 0.9, z - 0.1, x + 0.65, y + 0.9, z + 0.1, 255, 255, 255, 150 );
	        UIHelper.drawRectangle(x +0.35,   y + 0.9, z + 1.0, x - 0.1,  y + 0.9, z + 1.1, 255, 255, 255, 150 );
	        UIHelper.drawRectangle(x + 1.1,   y + 0.9, z + 1.0, x + 0.65, y + 0.9, z + 1.1, 255, 255, 255, 150 );				
			
			break;
		case WEST:
			UIHelper.drawRectangleEW(x + 0.1, y+ 0.35,  z - 0.1, x + 0.1, y , z, 255, 255, 255, 150 );
			UIHelper.drawRectangleEW(x + 0.1, y + 1.0,  z - 0.1, x + 0.1, y + 0.65,  z, 255, 255, 255, 150 );
			UIHelper.drawRectangleEW(x + 0.1, y + 0.35, z + 1.0, x + 0.1, y, z + 1.1, 255, 255, 255, 150 );
			UIHelper.drawRectangleEW(x + 0.1, y + 1.0,  z + 1.0, x + 0.1, y + 0.65,  z + 1.1, 255, 255, 255, 150 );			

			UIHelper.drawRectangleEW(x + 0.1, y, z - 0.1,  x + 0.1, y - 0.1, z + 0.35, 255, 255, 255, 150 );
			UIHelper.drawRectangleEW(x + 0.1, y, z + 0.65, x + 0.1, y - 0.1, z + 1.1, 255, 255, 255, 150 );			
			UIHelper.drawRectangleEW(x + 0.1, y + 1.1, z - 0.1,  x + 0.1, y + 1.0, z + 0.35, 255, 255, 255, 150 );
			UIHelper.drawRectangleEW(x + 0.1, y + 1.1, z + 0.65, x + 0.1, y + 1.0, z + 1.1, 255, 255, 255, 150 );			
			break;
		default:
			break;
		
		}
	}

}
