package mcp.mobius.waila.addons.thermalexpansion;

import java.util.List;

import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerEnergyCell implements IWailaDataProvider {

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
		if (!config.getConfig("thermalexpansion.energycell")) return currenttip;
		
		int energyReceive = accessor.getNBTInteger(accessor.getNBTData(), "Recv");
		int energySend    = accessor.getNBTInteger(accessor.getNBTData(), "Send");
		
		currenttip.add(String.format("%s/%s : %d / %d RF/t", LangUtil.translateG("hud.msg.in"), LangUtil.translateG("hud.msg.out"), energyReceive, energySend));
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		try {
			int recv = ThermalExpansionModule.TileEnergyCell_Recv.getInt(te);
			int send = ThermalExpansionModule.TileEnergyCell_Send.getInt(te);
			tag.setInteger("Recv", recv);
			tag.setInteger("Send", send);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return tag;
	}	
	
}
