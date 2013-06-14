package mcp.mobius.waila.addons.ic2;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

import mcp.mobius.waila.ConfigHandler;
import mcp.mobius.waila.mod_Waila;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.nei.api.API;
import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo.Layout;

public class HUDHandlerIC2Generator implements IHighlightHandler {

	private static Class  TEBaseGenerator = null;
	private static Method TEBaseGenerator_GetMaxOutput = null;
	
	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		return null;
	}	
	
	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, List<String> currenttip, Layout layout) {
		if (!ConfigHandler.instance().getConfig("ic2.outputeu"))
			return currenttip;
		
		TileEntity entity = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
		
		if ((entity != null) && (TEBaseGenerator.isInstance(entity))){
			int maxinput = 0;
			try{
				maxinput = (Integer)(TEBaseGenerator_GetMaxOutput.invoke(TEBaseGenerator.cast(entity)));
			} catch (Exception e){
				mod_Waila.instance.log.log(Level.SEVERE, "[IC2] Unhandled exception trying to access an TEBaseGenerator for display !.\n" + String.valueOf(e));
				return currenttip;				
			}
			currenttip.add(String.format("OUT : %s EU/t", maxinput));
		}
		
		return currenttip;
	}

	
	public static void register(){
		try{
			TEBaseGenerator = Class.forName("ic2.core.block.generator.tileentity.TileEntityBaseGenerator");
			TEBaseGenerator_GetMaxOutput = TEBaseGenerator.getMethod("getMaxEnergyOutput");
		} catch (ClassNotFoundException e){
			mod_Waila.instance.log.log(Level.WARNING, "[IC2] TEBaseGenerator class not found.");
		} catch (NoSuchMethodException e){
			mod_Waila.instance.log.log(Level.WARNING, "[IC2] One method was not found.");
		}
		
		if (TEBaseGenerator_GetMaxOutput != null){
			mod_Waila.instance.log.log(Level.INFO, "Waila module IndustrialCraft|Generator succefully hooked.");
		    API.registerHighlightHandler(new HUDHandlerIC2Generator(), Layout.BODY);
		}
	}	

}
