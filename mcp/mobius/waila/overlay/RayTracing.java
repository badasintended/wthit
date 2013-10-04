package mcp.mobius.waila.overlay;

import java.util.ArrayList;

import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo;
import mcp.mobius.waila.Constants;
import mcp.mobius.waila.addons.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class RayTracing {

	public static MovingObjectPosition raytracedTarget = null;
	
	public static void raytrace(){
		if (Minecraft.getMinecraft().objectMouseOver == null){
			raytracedTarget = null;
			return;
		}
			
		
		if(Minecraft.getMinecraft().objectMouseOver.typeOfHit == EnumMovingObjectType.ENTITY)
			raytracedTarget = Minecraft.getMinecraft().objectMouseOver;
		else
			if (ConfigHandler.instance().getConfig(Constants.CFG_WAILA_LIQUID))
				raytracedTarget = getLiquidRaytrace();
			else
				raytracedTarget = Minecraft.getMinecraft().objectMouseOver;
	}
	
    public static MovingObjectPosition getLiquidRaytrace()
    {
    	EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    	World world = Minecraft.getMinecraft().theWorld;
    	
    	if ((player == null) || (world == null)) return null;
    	
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)f + 1.62D - (double)player.yOffset;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
        Vec3 vec3 = world.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        return world.clip(vec3, vec31, true);
        
    }
    
    public static ArrayList<ItemStack> getIdentifierItems()
    {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        
    	if (raytracedTarget == null)
    		return items;
    	
    	EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    	World world = Minecraft.getMinecraft().theWorld; 
    	
        int x = raytracedTarget.blockX;
        int y = raytracedTarget.blockY;
        int z = raytracedTarget.blockZ;
        Block mouseoverBlock = Block.blocksList[world.getBlockId(x, y, z)];
        
        
        ArrayList<IHighlightHandler> handlers = new ArrayList<IHighlightHandler>();
        if(ItemInfo.highlightIdentifiers.get(0) != null)
        	handlers.addAll(ItemInfo.highlightIdentifiers.get(0));
        if(ItemInfo.highlightIdentifiers.get(mouseoverBlock.blockID) != null)
        	handlers.addAll(ItemInfo.highlightIdentifiers.get(mouseoverBlock.blockID));
        
        for(IHighlightHandler ident : handlers)
        {
            ItemStack item = ident.identifyHighlight(world, player, raytracedTarget);
            if(item != null)
                items.add(item);
        }
        
        if(items.size() > 0)
            return items;

        if (world.getBlockTileEntity(x, y, z) == null){
	        try{
	        	ItemStack block = new ItemStack(mouseoverBlock, 1, world.getBlockMetadata(x, y, z));
	        	items.add(block);
	        } catch(Exception e){
	        }
        }

        if(items.size() > 0)
            return items;

        ItemStack pick = mouseoverBlock.getPickBlock(raytracedTarget, world, x, y, z);
        if(pick != null)
            items.add(pick);
 
        if(items.size() > 0)
            return items;           

        try
        {
            items.addAll(mouseoverBlock.getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0));
        }
        catch(Exception e){}
        
        if(mouseoverBlock instanceof IShearable)
        {
            IShearable shearable = (IShearable)mouseoverBlock;
            if(shearable.isShearable(new ItemStack(Item.shears), world, x, y, z))
            {
                items.addAll(shearable.onSheared(new ItemStack(Item.shears), world, x, y, z, 0));
            }
        }
        
        //if(items.size() == 0)
        //   items.add(0, new ItemStack(mouseoverBlock, 1, world.getBlockMetadata(x, y, z)));
        
        return items;
    }	    
}
