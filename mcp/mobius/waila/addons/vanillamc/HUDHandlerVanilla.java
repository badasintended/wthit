package mcp.mobius.waila.addons.vanillamc;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.cbcore.LangUtil;

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
	static int cocoa		 = Block.cocoaPlant.blockID;
	static int netherwart    = Block.netherStalk.blockID;
	static int silverfish    = Block.silverfish.blockID;
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		int blockID       = accessor.getBlockID();
		
		if (blockID == silverfish){
			int metadata = accessor.getMetadata();
			switch(metadata){
			case 0:
				return new ItemStack(Block.stone);
			case 1:
				return new ItemStack(Block.cobblestone);
			case 2:
				return new ItemStack(Block.stoneBrick);
			default:
				return null;
			}
		}

		if (blockID == redstone){
			return new ItemStack(Item.redstone);
		}
		
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
				if (growthValue < 100.0)
					currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
				else
					currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
				return currenttip;
			}		

		if (blockID == cocoa){
		
			float growthValue = ((accessor.getMetadata() >> 2) / 2.0F) * 100.0F;
			if (growthValue < 100.0)
				currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
			else
				currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
			return currenttip;
		}		
		
		if (blockID == netherwart){
			float growthValue = (accessor.getMetadata() / 3.0F) * 100.0F;
			if (growthValue < 100.0)
				currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
			else
				currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
			return currenttip;
		}		
		
		if (config.getConfig("vanilla.leverstate"))
			if (blockID == leverID){
				String redstoneOn = (accessor.getMetadata() & 8) == 0 ? LangUtil.translateG("hud.msg.off") : LangUtil.translateG("hud.msg.on");
				currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.state"), redstoneOn));
				return currenttip;				
			}				

		if (config.getConfig("vanilla.repeater"))
			if ((blockID == repeaterIdle) ||(blockID == repeaterActv)){
				int tick = (accessor.getMetadata() >> 2) + 1 ;
				if (tick == 1)
					currenttip.add(String.format("%s : %s tick", LangUtil.translateG("hud.msg.delay"), tick));
				else
					currenttip.add(String.format("%s : %s ticks", LangUtil.translateG("hud.msg.delay"), tick));
				return currenttip;				
			}		

		if (config.getConfig("vanilla.comparator"))
			if ((blockID == comparatorIdl) ||(blockID == comparatorAct)){
				String mode = ((accessor.getMetadata() >> 2) & 1) == 0 ? LangUtil.translateG("hud.msg.comparator") : LangUtil.translateG("hud.msg.substractor");
				//int outputSignal = ((TileEntityComparator)entity).func_96100_a();
				currenttip.add("Mode : " + mode);
				//currenttip.add(String.format("Out : %s", outputSignal));
				return currenttip;				
			}		

		if (config.getConfig("vanilla.redstone"))
			if (blockID == redstone){
				currenttip.add(String.format("%s : %s" , LangUtil.translateG("hud.msg.power"), accessor.getMetadata()));
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
					currenttip.add(LangUtil.translateG("hud.msg.empty"));
				}
			}
		
		return currenttip;
	}	

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}	
	
	public static void register(){
		ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.spawntype");
		ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.growthvalue");
		ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.leverstate");
		ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.repeater");
		ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.comparator");
		ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.redstone");
		ModuleRegistrar.instance().addConfigRemote("VanillaMC", "vanilla.jukebox");
		
		IWailaDataProvider provider = new HUDHandlerVanilla();
		
		ModuleRegistrar.instance().registerStackProvider(provider, silverfish);
		ModuleRegistrar.instance().registerStackProvider(provider, redstone);
		ModuleRegistrar.instance().registerHeadProvider(provider, mobSpawnerID);
		ModuleRegistrar.instance().registerBodyProvider(provider, cropsID);
		ModuleRegistrar.instance().registerBodyProvider(provider, melonStemID);
		ModuleRegistrar.instance().registerBodyProvider(provider, pumpkinStemID);
		ModuleRegistrar.instance().registerBodyProvider(provider, carrotID);
		ModuleRegistrar.instance().registerBodyProvider(provider, potatoID);
		ModuleRegistrar.instance().registerBodyProvider(provider, leverID);
		ModuleRegistrar.instance().registerBodyProvider(provider, repeaterIdle);
		ModuleRegistrar.instance().registerBodyProvider(provider, repeaterActv);
		ModuleRegistrar.instance().registerBodyProvider(provider, comparatorIdl);
		ModuleRegistrar.instance().registerBodyProvider(provider, comparatorAct);
		ModuleRegistrar.instance().registerHeadProvider(provider, redstone);
		ModuleRegistrar.instance().registerBodyProvider(provider, redstone);
		ModuleRegistrar.instance().registerBodyProvider(provider, jukebox);
		ModuleRegistrar.instance().registerBodyProvider(provider, cocoa);
		ModuleRegistrar.instance().registerBodyProvider(provider, netherwart);			
		
		ModuleRegistrar.instance().registerDocTextFile("/mcp/mobius/waila/addons/vanillamc/WikiData.csv");
		
		//ExternalModulesHandler.instance().registerBlockDecorator(new HUDDecoratorVanilla(), repeaterIdle);
	}


	
}
