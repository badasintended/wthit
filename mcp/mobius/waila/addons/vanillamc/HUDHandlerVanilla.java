package mcp.mobius.waila.addons.vanillamc;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerVanilla implements IWailaDataProvider {

	static int mobSpawnerID  = Block.mobSpawner.blockID;
	static int cropsID       = Block.crops.blockID;
	static int melonStemID   = Block.melonStem.blockID;
	static int pumpkinStemID = Block.pumpkinStem.blockID;
	static int carrotID      = Block.carrot.blockID;
	static int potatoID      = Block.potato.blockID;
	
	
	@Override
	public ItemStack getWailaStack(World world, EntityPlayer player, MovingObjectPosition mop, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, List<String> currenttip, IWailaConfigHandler config) {
		int blockID       = world.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
		TileEntity entity = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
		
		/* Mob spawner handler */
		if (blockID == mobSpawnerID && entity instanceof TileEntityMobSpawner && config.getConfig("vanilla.showspawntype")){
			String name = currenttip.get(0);
			String mobname = ((TileEntityMobSpawner)entity).func_98049_a().getEntityNameToSpawn();
			currenttip.set(0, String.format("%s (%s)", name, mobname));
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, List<String> currenttip, IWailaConfigHandler config) {
		int blockID       = world.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
		
		/* Crops */
		if (config.getConfig("vanilla.showgrowthvalue"))
			if (blockID == cropsID || blockID == melonStemID || blockID == pumpkinStemID || blockID == carrotID || blockID == potatoID){
				float growthValue = ((float)world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ) / 7.0F) * 100.0F;
				if (growthValue != 100.0)
					currenttip.add(String.format("Growth : %.0f %%", growthValue));
				else
					currenttip.add("Growth : Mature");
			}		
		
		return currenttip;
	}

	public static void register(){
		ExternalModulesHandler.instance().addConfig("VanillaMC", "vanilla.showspawntype", "Spawner type");
		ExternalModulesHandler.instance().addConfig("VanillaMC", "vanilla.showgrowthvalue", "Growth value");		
		ExternalModulesHandler.instance().registerHeadProvider(new HUDHandlerVanilla(), mobSpawnerID);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), cropsID);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), melonStemID);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), pumpkinStemID);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), carrotID);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerVanilla(), potatoID);		
	}
	
}
