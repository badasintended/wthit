package mcp.mobius.waila.addons.stevescarts;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDCartAssembler implements IWailaDataProvider {

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
		/*
		if (!config.getConfig("stevescarts.showall")) return currenttip;
		if (config.getConfig("stevescarts.shifttoggle") && !accessor.getPlayer().isSneaking()){
			currenttip.add(ITALIC + "Press shift for more data");
			return currenttip;			
		}
		
		int currTime      = accessor.getNBTInteger(accessor.getNBTData(), "currentTime");
		int maxTime       = accessor.getNBTInteger(accessor.getNBTData(), "maxTime");
		int isAssembling  = accessor.getNBTInteger(accessor.getNBTData(), "isAssembling");
		NBTTagList slots  = accessor.getNBTData().getTagList("Items");
		boolean hasOutput = false;
		Item ItemCartModule = null;
		try {
			ItemCartModule = (Item)StevesCartsModule.ItemCartModule.get(null);
		} catch (Exception e) {
			currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
			return currenttip;
		}
		
		for (int i = 0; i < slots.tagCount(); i++){
			NBTTagCompound subtag = (NBTTagCompound)(slots.tagAt(i));
			byte slot = subtag.getByte("Slot");
			if (slot == 30)
				hasOutput = true;
		}
		
		if (isAssembling == 1){
			currenttip.add(String.format("Time remaining : %.1f sec", (maxTime - currTime) / 20.0f));
			
			NBTTagCompound output = accessor.getNBTData().getCompoundTag("Output");
			short outID = output.getShort("id");
			byte[] metas = output.getCompoundTag("tag").getByteArray("Modules"); 
			
			for (byte b : metas){
				currenttip.add(new ItemStack(ItemCartModule, 1, b).getDisplayName());
			}
		}
		else
			if (hasOutput)
				currenttip.add(String.format("Time remaining : <Done>"));
			else
				currenttip.add(String.format("Time remaining : <Idle>"));
		*/
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}


}
