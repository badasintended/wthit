package mcp.mobius.waila.addons.ic2;

import java.util.List;

import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDHandlerTEBaseGenerator implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		
		try{
			int    storage = -1;
			int production = -1;
			
			if (IC2Module.TileEntityBaseGenerator.isInstance(accessor.getTileEntity())){
				storage    = IC2Module.TEBG_MaxStorage.getShort(accessor.getTileEntity());
				production = IC2Module.TEBG_Production.getInt(accessor.getTileEntity());
			}
			else if (IC2Module.TileEntityGeoGenerator.isInstance(accessor.getTileEntity())){
				storage    = IC2Module.TEGG_MaxStorage.getShort(accessor.getTileEntity());
				production = IC2Module.TEGG_Production.getInt(accessor.getTileEntity());
			}
			else if (IC2Module.TileEntitySemifluidGenerator.isInstance(accessor.getTileEntity())){
				storage    = IC2Module.TESG_MaxStorage.getShort(accessor.getTileEntity());
				production = (int)IC2Module.TESG_Production.getDouble(accessor.getTileEntity());
			}
		
			String storedStr  = LangUtil.translateG("hud.msg.stored");
			String outputStr  = LangUtil.translateG("hud.msg.output");			
			
			/* EU Storage */
			if (ConfigHandler.instance().getConfig("ic2.storage")){
				double stored  = IC2Module.getStoredEnergy(accessor); 
			
				if ( stored >= 0.0 && storage > 0)
					currenttip.add(String.format("%s%s\u00a7f%d\u00a7r / \u00a7f%d\u00a7r EU", storedStr, TAB + ALIGNRIGHT, Math.round(Math.min(stored,storage)), storage));
				
				//else if (stored >= 0.0)
				//	currenttip.add(String.format("Stored : %d EU", stored));				
			}
			
			if (ConfigHandler.instance().getConfig("ic2.outputeu")){
					if ( production > 0)
						currenttip.add(String.format("%s%s\u00a7f%d\u00a7r EU/t", outputStr, TAB + ALIGNRIGHT, production));
			}
			
		} catch (Exception e){    
			currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
		} 		
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}	
	
}
