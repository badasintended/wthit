package mcp.mobius.waila.addons.buildcraft;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

//import buildcraft.factory.TileTank;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;
import codechicken.nei.api.ItemInfo.Layout;
import codechicken.nei.api.API;
import codechicken.nei.api.IHighlightHandler;

public class HUDHandlerBCTanks implements IHighlightHandler {
	
	private static Class  TileTank = null;
	private static Method TileTank_GetTanks = null;
	
	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		return null;
	}

	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop,	List<String> currenttip, Layout layout) {

		TileEntity entity = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
		
		if ((entity != null) && (TileTank.isInstance(entity))){
			
			ILiquidTank tank = null;
			try{
				tank = ((ILiquidTank[])TileTank_GetTanks.invoke(TileTank.cast(entity), ForgeDirection.UNKNOWN))[0];
			} catch (Exception e){
				mod_Waila.instance.log.log(Level.SEVERE, "[BC] Unhandled exception trying to access a tank for display !.\n" + String.valueOf(e));
				return currenttip;
			}
			
					
			LiquidStack liquidStack = tank.getLiquid();
			int liquidAmount = liquidStack != null ? liquidStack.amount:0;
			int capacity     = tank.getCapacity();
			
			if (liquidStack != null && layout == Layout.HEADER && ConfigHandler.instance().getConfig("bc.tanktype")){
				String name = currenttip.get(0);
				name = name + " (" + liquidStack.asItemStack().getDisplayName() + ")";
				currenttip.set(0, name);
			}
			
			if (liquidStack == null && layout == Layout.HEADER && ConfigHandler.instance().getConfig("bc.tanktype")){
				String name = currenttip.get(0);
				name = name + " <Empty>";
				currenttip.set(0, name);
			}
			
			if (layout == Layout.BODY && ConfigHandler.instance().getConfig("bc.tankamount"))
				currenttip.add(String.valueOf(liquidAmount) + "/" + String.valueOf(capacity)  + " mB");

		}
		return currenttip;
	}
	
	
	
	public static void register(){
		try{
			TileTank = Class.forName("buildcraft.factory.TileTank");
			TileTank_GetTanks   = TileTank.getMethod("getTanks", ForgeDirection.class);
			
		} catch (ClassNotFoundException e){
			mod_Waila.instance.log.log(Level.WARNING, "[BC] TileTank class not found.");
		} catch (NoSuchMethodException e){
			mod_Waila.instance.log.log(Level.WARNING, "[BB] getTanks() not found.");
		}
		
		if (TileTank_GetTanks != null){
			mod_Waila.instance.log.log(Level.INFO, "Waila module BuildcraftTank succefully hooked.");
			API.registerHighlightHandler(new HUDHandlerBCTanks(), Layout.HEADER);
		    API.registerHighlightHandler(new HUDHandlerBCTanks(), Layout.BODY);
		}
	}	
	
}
