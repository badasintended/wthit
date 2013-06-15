package mcp.mobius.waila.addons.ic2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.nei.api.API;
import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo.Layout;

public class HUDHandlerIC2Machine implements IHighlightHandler {

	private static Class  TEElectricMachine = null;
	private static Method TEElectricMachine_GetMaxSafeInput = null;
	
	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		return null;
	}	
	
	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, List<String> currenttip, Layout layout) {
		if (!ConfigHandler.instance().getConfig("ic2.inputeu"))
			return currenttip;		
		
		TileEntity entity = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
		
		if ((entity != null) && (TEElectricMachine.isInstance(entity))){
			int maxinput = 0;
			try{
				maxinput = (Integer)(TEElectricMachine_GetMaxSafeInput.invoke(TEElectricMachine.cast(entity)));
			} catch (Exception e){
				mod_Waila.instance.log.log(Level.SEVERE, "[IC2] Unhandled exception trying to access an IEnergyStorage for display !.\n" + String.valueOf(e));
				return currenttip;				
			}
			currenttip.add(String.format("IN : %s EU/t", maxinput));
		}
		
		return currenttip;
	}

	
	public static void register(){
		try{
			TEElectricMachine = Class.forName("ic2.core.block.machine.tileentity.TileEntityElectricMachine");
			TEElectricMachine_GetMaxSafeInput = TEElectricMachine.getMethod("getMaxSafeInput");
		} catch (ClassNotFoundException e){
			mod_Waila.instance.log.log(Level.WARNING, "[IC2] TileEntityElectricMachine class not found.");
		} catch (NoSuchMethodException e){
			mod_Waila.instance.log.log(Level.WARNING, "[IC2] One method was not found.");
		}
		
		if (TEElectricMachine_GetMaxSafeInput != null){
			mod_Waila.instance.log.log(Level.INFO, "Waila module IndustrialCraft|Machine succefully hooked.");
		    API.registerHighlightHandler(new HUDHandlerIC2Machine(), Layout.BODY);
		}
	}	

}
