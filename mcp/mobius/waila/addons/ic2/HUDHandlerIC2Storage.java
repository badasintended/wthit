package mcp.mobius.waila.addons.ic2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

import mcp.mobius.waila.ConfigHandler;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.buildcraft.HUDHandlerBCTanks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import codechicken.nei.api.API;
import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo.Layout;

public class HUDHandlerIC2Storage implements IHighlightHandler {

	private static Class  TEElectricBlock = null;
	private static Field  TEElectricBlock_Energy      = null;
	private static Method TEElectricBlock_GetStored   = null;
	private static Method TEElectricBlock_GetCapacity = null;
	private static Method TEElectricBlock_GetOutput   = null;	
	private static Method TEElectricBlock_GetMaxSafeInput = null;
	
	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		return null;
	}	
	
	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, List<String> currenttip, Layout layout) {
		TileEntity entity = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
		
		if ((entity != null) && (TEElectricBlock.isInstance(entity))){
			int stored = 0;
			int capacity = 0;
			int output = 0;
			int maxinput = 0;
			try{
				//stored   = (Integer)(TEElectricBlock_Energy.get(entity));
				//capacity = (Integer)(TEElectricBlock_GetCapacity.invoke(TEElectricBlock.cast(entity)));
				output   = (Integer)(TEElectricBlock_GetOutput.invoke(TEElectricBlock.cast(entity)));
				maxinput = (Integer)(TEElectricBlock_GetMaxSafeInput.invoke(TEElectricBlock.cast(entity)));
			} catch (Exception e){
				mod_Waila.instance.log.log(Level.SEVERE, "[IC2] Unhandled exception trying to access an IEnergyStorage for display !.\n" + String.valueOf(e));
				return currenttip;				
			}
			if (ConfigHandler.instance().getConfig("ic2.outputeu") && ConfigHandler.instance().getConfig("ic2.inputeu"))
				currenttip.add(String.format("IN/OUT : %s/%s EU/t", maxinput, output));
			else if (ConfigHandler.instance().getConfig("ic2.outputeu") && !ConfigHandler.instance().getConfig("ic2.inputeu"))
				currenttip.add(String.format("OUT : %s EU/t", output));
			else if (!ConfigHandler.instance().getConfig("ic2.outputeu") && ConfigHandler.instance().getConfig("ic2.inputeu"))
				currenttip.add(String.format("IN : %s EU/t", maxinput));			
			//currenttip.add(String.format("%s / %s EU", stored, capacity));			
		}
		
		return currenttip;
	}

	
	public static void register(){
		try{
			TEElectricBlock = Class.forName("ic2.core.block.wiring.TileEntityElectricBlock");
			TEElectricBlock_GetStored	    = TEElectricBlock.getMethod("getStored");
			TEElectricBlock_GetCapacity	    = TEElectricBlock.getMethod("getCapacity");
			TEElectricBlock_GetOutput	    = TEElectricBlock.getMethod("getOutput");
			TEElectricBlock_GetMaxSafeInput = TEElectricBlock.getMethod("getMaxSafeInput");
			TEElectricBlock_Energy          = TEElectricBlock.getField("energy");
		} catch (ClassNotFoundException e){
			mod_Waila.instance.log.log(Level.WARNING, "[IC2] IEnergyStorage class not found.");
		} catch (NoSuchMethodException e){
			mod_Waila.instance.log.log(Level.WARNING, "[IC2] One method was not found.");
		} catch (NoSuchFieldException e){
			mod_Waila.instance.log.log(Level.WARNING, "[IC2] One field was not found.");
		}
		
		
		if (TEElectricBlock_Energy != null){
			mod_Waila.instance.log.log(Level.INFO, "Waila module IndustrialCraft|Storage succefully hooked.");
		    API.registerHighlightHandler(new HUDHandlerIC2Storage(), Layout.BODY);
		}
	}		
	
}
