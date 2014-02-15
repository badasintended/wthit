package mcp.mobius.waila.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorBlock;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class WailaPacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player){
    	try{
			if (packet.channel.equals("Waila")) {
	
					byte header = this.getHeader(packet);
					if (header == 0x00){
						Packet0x00ServerPing castedPacket = new Packet0x00ServerPing(packet);
						Waila.log.info("Received server authentication msg. Remote sync will be activated");
						Waila.instance.serverPresent = true;
					
						for (String key : castedPacket.forcedKeys.keySet())
							Waila.log.info(String.format("Received forced key config %s : %s", key, castedPacket.forcedKeys.get(key)));
						
						ConfigHandler.instance().forcedConfigs = castedPacket.forcedKeys;
					}
					else if (header == 0x01){
						Packet0x01TERequest castedPacket = new Packet0x01TERequest(packet);
				        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				        TileEntity      entity = DimensionManager.getWorld(castedPacket.worldID).getBlockTileEntity(castedPacket.posX, castedPacket.posY, castedPacket.posZ);
				        //TileEntity      entity = server.worldServers[castedPacket.worldID].getBlockTileEntity(castedPacket.posX, castedPacket.posY, castedPacket.posZ);
				        if (entity != null){
				        	try{
				        		NBTTagCompound tag = new NBTTagCompound();
				        		entity.writeToNBT(tag);
				        		PacketDispatcher.sendPacketToPlayer(Packet0x02TENBTData.create(tag), player);
				        	}catch(Throwable e){
				        		WailaExceptionHandler.handleErr(e, entity.getClass().toString(), null);
				        	}
				        }
					}
					else if (header == 0x02){
						Packet0x02TENBTData castedPacket = new Packet0x02TENBTData(packet);
						DataAccessorBlock.instance.remoteNbt = castedPacket.tag;
					}
				}
	        }
		catch (Exception e){
			return;
		}		
	}

	public byte getHeader(Packet250CustomPayload packet){
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		try{
			return inputStream.readByte();
		} catch (IOException e){
			return -1;
		}
	}
	
		
}
