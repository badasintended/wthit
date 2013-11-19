package mcp.mobius.waila.addons.vanillamc;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class HUDDecoratorVanilla implements IWailaBlockDecorator {

	@Override
	public void decorateBlock(ItemStack itemStack, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
	
        double offset = 0.1;
        double delta = 1 + 2 * offset;
        
        double x = accessor.getRenderingPosition().xCoord - offset;
        double y = accessor.getRenderingPosition().yCoord - offset;
        double z = accessor.getRenderingPosition().zCoord - offset;
		
		Tessellator tessellator = Tessellator.instance;
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

        tessellator.addVertex(x + delta,          y + 0.2 , z);
        tessellator.addVertex(x + delta,          y + 0.2 , z + delta/2 - 0.1);
        tessellator.addVertex(x + delta + offset, y + 0.2,  z + delta/2 - 0.1);
        tessellator.addVertex(x + delta + offset, y + 0.2,  z);

        tessellator.addVertex(x + delta,          y + 0.2 , z + delta/2 + 0.1);
        tessellator.addVertex(x + delta,          y + 0.2 , z + delta);
        tessellator.addVertex(x + delta + offset, y + 0.2,  z + delta);
        tessellator.addVertex(x + delta + offset, y + 0.2,  z + delta/2 + 0.1);        
        
        
        
        tessellator.addVertex(x+ 0.1,           y + 0.2,  z);
        tessellator.addVertex(x+ 0.1,           y + 0.2,  z+ offset);
        tessellator.addVertex(x+ delta/2 - 0.1, y + 0.2,  z+ offset);
        tessellator.addVertex(x+ delta/2 - 0.1, y + 0.2,  z);

        tessellator.addVertex(x+ delta/2 + 0.1, y + 0.2,  z);
        tessellator.addVertex(x+ delta/2 + 0.1, y + 0.2,  z+ offset);
        tessellator.addVertex(x+ delta,         y + 0.2,  z+ offset);
        tessellator.addVertex(x+ delta,         y + 0.2,  z);        

        tessellator.addVertex(x,                y + 0.2,  z+ delta);
        tessellator.addVertex(x,                y + 0.2,  z+ offset+ delta);
        tessellator.addVertex(x+ delta/2 - 0.1, y + 0.2,  z+ offset+ delta);
        tessellator.addVertex(x+ delta/2 - 0.1, y + 0.2,  z+ delta);

        tessellator.addVertex(x+ delta/2 + 0.1, y + 0.2,  z+ delta);
        tessellator.addVertex(x+ delta/2 + 0.1, y + 0.2,  z+ offset+ delta);
        tessellator.addVertex(x+ delta + 0.1,   y + 0.2,  z+ offset+ delta);
        tessellator.addVertex(x+ delta + 0.1,   y + 0.2,  z+ delta);        
        
        tessellator.draw();		
		
	}

}
