package mcp.mobius.waila.addons.ic2;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDHandlerTEGenerator implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		try{
			double storage    = accessor.getNBTData().getDouble("storage");
			int    production = accessor.getNBTData().getInteger("production");
			long   maxStorage = accessor.getNBTData().getLong("maxStorage");
			
			String storedStr  = LangUtil.translateG("hud.msg.stored");
			String outputStr  = LangUtil.translateG("hud.msg.output");			
			
			/* EU Storage */
			if (ConfigHandler.instance().getConfig("ic2.storage")){
				if (maxStorage > 0)
					currenttip.add(String.format("%s%s\u00a7f%d\u00a7r / \u00a7f%d\u00a7r EU", storedStr, TAB + ALIGNRIGHT, Math.round(Math.min(storage,maxStorage)), maxStorage));
			}
			
			if (ConfigHandler.instance().getConfig("ic2.outputeu")){
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
	public NBTTagCompound getNBTData(TileEntity te, NBTTagCompound tag,	World world, int x, int y, int z) {
		
		try{
			double storage    = -1;
			long   production = -1;
			long   maxStorage = -1;
			
			if (IC2Module.TileBaseGenerator.isInstance(te)){
				storage    = IC2Module.TileBaseGenerator_storage.getDouble(te);
				production = (long)IC2Module.TileBaseGenerator_production.getInt(te);
				maxStorage = (long)IC2Module.TileBaseGenerator_maxStorage.getShort(te);
			}

			else if (IC2Module.TileGeoGenerator.isInstance(te)){
				storage    = IC2Module.TileGeoGenerator_storage.getDouble(te);
				production = (long)IC2Module.TileGeoGenerator_production.getInt(te);
				maxStorage = (long)IC2Module.TileGeoGenerator_maxStorage.getShort(te);				
			}
			
			else if (IC2Module.TileKineticGenerator.isInstance(te)){
				storage    = IC2Module.TileKineticGenerator_storage.getDouble(te);
				production = MathHelper.floor_double_long(IC2Module.TileKineticGenerator_production.getDouble(te));
				maxStorage = (long)IC2Module.TileKineticGenerator_maxStorage.getInt(te);				
			}			
			
			else if (IC2Module.TileSemifluidGenerator.isInstance(te)){
				storage    = IC2Module.TileSemifluidGenerator_storage.getDouble(te);
				production = MathHelper.floor_double_long(IC2Module.TileSemifluidGenerator_production.getDouble(te));
				maxStorage = (long)IC2Module.TileSemifluidGenerator_maxStorage.getShort(te);				
			}			
			
			else if (IC2Module.TileStirlingGenerator.isInstance(te)){
				storage    = IC2Module.TileStirlingGenerator_storage.getDouble(te);
				production = MathHelper.floor_double_long(IC2Module.TileStirlingGenerator_production.getDouble(te));
				maxStorage = (long)IC2Module.TileStirlingGenerator_maxStorage.getShort(te);				
			}			
			
			tag.setDouble ("storage",    storage);
			tag.setLong   ("production", production);
			tag.setLong   ("maxStorage", maxStorage);
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return tag;
	}

}
