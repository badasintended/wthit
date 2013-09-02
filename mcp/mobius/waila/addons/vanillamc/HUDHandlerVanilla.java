package mcp.mobius.waila.addons.vanillamc;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerVanilla implements IWailaDataProvider {

	static int mobSpawnerID  = Block.mobSpawner.blockID;
	static int cropsID       = Block.crops.blockID;
	static int melonStemID   = Block.melonStem.blockID;
	static int pumpkinStemID = Block.pumpkinStem.blockID;
	static int carrotID      = Block.carrot.blockID;
	static int potatoID      = Block.potato.blockID;
	static int leverID       = Block.lever.blockID;
	static int repeaterIdle  = Block.redstoneRepeaterIdle.blockID;
	static int repeaterActv  = Block.redstoneRepeaterActive.blockID;
	static int comparatorIdl = Block.redstoneComparatorIdle.blockID;
	static int comparatorAct = Block.redstoneComparatorActive.blockID;
	static int redstone      = Block.redstoneWire.blockID;
	static int jukebox       = Block.jukebox.blockID;
	

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		int blockID       = accessor.getBlockID();
		
		/* Mob spawner handler */
		if (blockID == mobSpawnerID && accessor.getTileEntity() instanceof TileEntityMobSpawner && config.getConfig("vanilla.spawntype")){
			String name = currenttip.get(0);
			String mobname = ((TileEntityMobSpawner)accessor.getTileEntity()).getSpawnerLogic().getEntityNameToSpawn();
			currenttip.set(0, String.format("%s (%s)", name, mobname));
		}

		if (blockID == redstone){
			String name = currenttip.get(0).replaceFirst(String.format(" %s", accessor.getMetadata()), "");
			currenttip.set(0, name);
		}		
		
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		int blockID       = accessor.getBlockID();
		/* Crops */
		if (config.getConfig("vanilla.growthvalue"))
			if (blockID == cropsID || blockID == melonStemID || blockID == pumpkinStemID || blockID == carrotID || blockID == potatoID){
				float growthValue = (accessor.getMetadata() / 7.0F) * 100.0F;
				if (growthValue != 100.0)
					currenttip.add(String.format("Growth : %.0f %%", growthValue));
				else
					currenttip.add("Growth : Mature");
				return currenttip;
			}		

		if (config.getConfig("vanilla.leverstate"))
			if (blockID == leverID){
				String redstoneOn = (accessor.getMetadata() & 8) == 0 ? "Off" : "On";
				currenttip.add("State : " + redstoneOn);
				return currenttip;				
			}				

		if (config.getConfig("vanilla.repeater"))
			if ((blockID == repeaterIdle) ||(blockID == repeaterActv)){
				int tick = (accessor.getMetadata() >> 2) + 1 ;
				if (tick == 1)
					currenttip.add(String.format("Delay : %s tick", tick));
				else
					currenttip.add(String.format("Delay : %s ticks", tick));
				return currenttip;				
			}		

		if (config.getConfig("vanilla.comparator"))
			if ((blockID == comparatorIdl) ||(blockID == comparatorAct)){
				String mode = ((accessor.getMetadata() >> 2) & 1) == 0 ? "Comparator" : "Subtractor";
				//int outputSignal = ((TileEntityComparator)entity).func_96100_a();
				currenttip.add("Mode : " + mode);
				//currenttip.add(String.format("Out : %s", outputSignal));
				return currenttip;				
			}		

		if (config.getConfig("vanilla.redstone"))
			if (blockID == redstone){
				currenttip.add(String.format("Power : %s" , accessor.getMetadata()));
				return currenttip;				
			}	
		
		if (config.getConfig("vanilla.jukebox"))
			if (blockID == jukebox){
				NBTTagCompound tag = accessor.getNBTData();
				Item record = null;
				if (tag.hasKey("Record")){
					record = Item.itemsList[accessor.getNBTInteger(tag, "Record")];
					currenttip.add(((ItemRecord)record).getRecordTitle());					
				} else {
					currenttip.add("<Empty>");
				}
			}
		
		return currenttip;
	}	
	
	public static void register(){
		ExternalModulesHandler.instance().addConfig("VanillaMC", "vanilla.spawntype", "Spawner type");
		ExternalModulesHandler.instance().addConfig("VanillaMC", "vanilla.growthvalue", "Growth value");
		ExternalModulesHandler.instance().addConfig("VanillaMC", "vanilla.leverstate", "Lever state");
		ExternalModulesHandler.instance().addConfig("VanillaMC", "vanilla.repeater", "Repeater delay");
		ExternalModulesHandler.instance().addConfig("VanillaMC", "vanilla.comparator", "Comparator mode");
		ExternalModulesHandler.instance().addConfig("VanillaMC", "vanilla.redstone", "Redstone power");
		ExternalModulesHandler.instance().addConfigRemote("VanillaMC", "vanilla.jukebox", "Jukebox disk");		
		ExternalModulesHandler.instance().registerHeadProvider(new HUDHandlerVanilla(), mobSpawnerID);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), cropsID);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), melonStemID);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), pumpkinStemID);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), carrotID);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), potatoID);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), leverID);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), repeaterIdle);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), repeaterActv);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), comparatorIdl);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), comparatorAct);
		ExternalModulesHandler.instance().registerHeadProvider(new HUDHandlerVanilla(), redstone);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), redstone);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), jukebox);	
		
		ExternalModulesHandler.instance().registerDocTextFile("/mcp/mobius/waila/addons/vanillamc/WikiData.csv");		
	}


	
}
