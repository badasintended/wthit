package mcp.mobius.waila.addons.thaumcraft;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerIAspectContainer implements IWailaDataProvider{

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
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag,	World world, int x, int y, int z) {
	
		try{
			/*
			HashMap knownAspects = (HashMap)ThaumcraftModule.CommonProxy_getKnownAspects.invoke(ThaumcraftModule.Thaumcraft_proxy.get(null)); 
			
			for (Object o : knownAspects.keySet()){
				NBTTagCompound knownAspectsNBT = new NBTTagCompound();
				ThaumcraftModule.AspectList_writeToNBT.invoke(knownAspects.get(o), knownAspectsNBT);
				NBTTagList aspects = knownAspectsNBT.getTagList("Aspects", 10);
				
				for (int i = 0; i < aspects.tagCount(); i++){
					NBTTagCompound entry = aspects.getCompoundTagAt(i);
					System.out.printf("%s %s %s\n",o ,entry.getString("key"), entry.getInteger("amount"));
				}
			
			}
			*/
			
			/*
			ThaumcraftModule.AspectList_writeToNBT.invoke(ThaumcraftModule.IAspectContainer_getAspects.invoke(te), tag);
			
			NBTTagList aspects =  tag.getTagList("Aspects", 10);
			
			for (int i = 0; i < aspects.tagCount(); i++){
				NBTTagCompound entry = aspects.getCompoundTagAt(i);
				System.out.printf("%s %s\n", entry.getString("key"), entry.getInteger("amount"));
			}
			*/
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return tag;
	}

}
