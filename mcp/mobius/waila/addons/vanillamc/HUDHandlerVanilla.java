package mcp.mobius.waila.addons.vanillamc;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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

	//TODO : Fix mobspawners
	//TODO : Fix records
	
	static Block mobSpawner    = Block.getBlockFromName("mob_spawner");
	static Block crops         = Block.getBlockFromName("wheat");
	static Block melonStem     = Block.getBlockFromName("melon_stem");
	static Block pumpkinStem   = Block.getBlockFromName("pumpkin_stem");
	static Block carrot        = Block.getBlockFromName("carrots");
	static Block potato        = Block.getBlockFromName("potatoes");
	static Block lever         = Block.getBlockFromName("lever");
	static Block repeaterIdle  = Block.getBlockFromName("unpowered_repeater");
	static Block repeaterActv  = Block.getBlockFromName("powered_repeater");
	static Block comparatorIdl = Block.getBlockFromName("unpowered_comparator");
	static Block comparatorAct = Block.getBlockFromName("powered_comparator");
	static Block redstone      = Block.getBlockFromName("redstone_wire");
	static Block jukebox       = Block.getBlockFromName("jukebox");
	static Block cocoa		   = Block.getBlockFromName("cocoa");
	static Block netherwart    = Block.getBlockFromName("nether_wart");
	static Block silverfish    = Block.getBlockFromName("monster_egg");
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		Block block = accessor.getBlock();
		
		if (block.equals(silverfish)){
			int metadata = accessor.getMetadata();
			switch(metadata){
			case 0:
				return new ItemStack(Blocks.stone);
			case 1:
				return new ItemStack(Blocks.cobblestone);
			case 2:
				return new ItemStack(Blocks.brick_block);
			default:
				return null;
			}
		}

		if (block.equals(redstone)){
			return new ItemStack(Items.redstone);
		}
		
		return null;
		
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		Block block       = accessor.getBlock();
		
		/* Mob spawner handler */
		/*
		if (block == mobSpawner && accessor.getTileEntity() instanceof TileEntityMobSpawner && config.getConfig("vanilla.spawntype")){
			String name = currenttip.get(0);
			String mobname = ((TileEntityMobSpawner)accessor.getTileEntity()).getSpawnerLogic().getEntityNameToSpawn();
			currenttip.set(0, String.format("%s (%s)", name, mobname));
		}
		*/

		if (block == redstone){
			String name = currenttip.get(0).replaceFirst(String.format(" %s", accessor.getMetadata()), "");
			currenttip.set(0, name);
		}		
		
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		Block block       = accessor.getBlock();
		/* Crops */
		if (config.getConfig("vanilla.growthvalue"))
			if (block == crops || block == melonStem || block == pumpkinStem || block == carrot || block == potato){
				float growthValue = (accessor.getMetadata() / 7.0F) * 100.0F;
				if (growthValue < 100.0)
					currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
				else
					currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
				return currenttip;
			}		

		if (block == cocoa){
		
			float growthValue = ((accessor.getMetadata() >> 2) / 2.0F) * 100.0F;
			if (growthValue < 100.0)
				currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
			else
				currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
			return currenttip;
		}		
		
		if (block == netherwart){
			float growthValue = (accessor.getMetadata() / 3.0F) * 100.0F;
			if (growthValue < 100.0)
				currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
			else
				currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
			return currenttip;
		}		
		
		if (config.getConfig("vanilla.leverstate"))
			if (block == lever){
				String redstoneOn = (accessor.getMetadata() & 8) == 0 ? LangUtil.translateG("hud.msg.off") : LangUtil.translateG("hud.msg.on");
				currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.state"), redstoneOn));
				return currenttip;				
			}				

		if (config.getConfig("vanilla.repeater"))
			if ((block == repeaterIdle) ||(block == repeaterActv)){
				int tick = (accessor.getMetadata() >> 2) + 1 ;
				if (tick == 1)
					currenttip.add(String.format("%s : %s tick", LangUtil.translateG("hud.msg.delay"), tick));
				else
					currenttip.add(String.format("%s : %s ticks", LangUtil.translateG("hud.msg.delay"), tick));
				return currenttip;				
			}		

		if (config.getConfig("vanilla.comparator"))
			if ((block == comparatorIdl) ||(block == comparatorAct)){
				String mode = ((accessor.getMetadata() >> 2) & 1) == 0 ? LangUtil.translateG("hud.msg.comparator") : LangUtil.translateG("hud.msg.substractor");
				//int outputSignal = ((TileEntityComparator)entity).func_96100_a();
				currenttip.add("Mode : " + mode);
				//currenttip.add(String.format("Out : %s", outputSignal));
				return currenttip;				
			}		

		if (config.getConfig("vanilla.redstone"))
			if (block == redstone){
				currenttip.add(String.format("%s : %s" , LangUtil.translateG("hud.msg.power"), accessor.getMetadata()));
				return currenttip;				
			}	
		
		/*
		if (config.getConfig("vanilla.jukebox"))
			if (block == jukebox){
				NBTTagCompound tag = accessor.getNBTData();
				Item record = null;
				if (tag.hasKey("Record")){
					record = Item.itemsList[accessor.getNBTInteger(tag, "Record")];
					currenttip.add(((ItemRecord)record).getRecordTitle());					
				} else {
					currenttip.add(LangUtil.translateG("hud.msg.empty"));
				}
			}
		*/
		
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
		
		ModuleRegistrar.instance().registerStackProvider(provider, silverfish.getClass());
		ModuleRegistrar.instance().registerStackProvider(provider, redstone.getClass());
		ModuleRegistrar.instance().registerHeadProvider(provider,  mobSpawner.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  crops.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  melonStem.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  pumpkinStem.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  carrot.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  potato.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  lever.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  repeaterIdle.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  repeaterActv.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  comparatorIdl.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  comparatorAct.getClass());
		ModuleRegistrar.instance().registerHeadProvider(provider,  redstone.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  redstone.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  jukebox.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  cocoa.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider,  netherwart.getClass());			
		
		//ModuleRegistrar.instance().registerDocTextFile("/mcp/mobius/waila/addons/vanillamc/WikiData.csv");
		
		//ExternalModulesHandler.instance().registerBlockDecorator(new HUDDecoratorVanilla(), repeaterIdle);
	}


	
}
