package mcp.mobius.waila.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.handlers.DataAccessor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class WailaPacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player){
        if (packet.channel.equals("Waila")) {
			byte header = this.getHeader(packet);
			if (header == 0x00){
				mod_Waila.log.info("Received server authentication msg. Remote sync will be activated");
				mod_Waila.instance.serverPresent = true;
			}
			else if (header == 0x01){
				try{
					Packet0x01TERequest castedPacket = new Packet0x01TERequest(packet);
			        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			        TileEntity      entity = server.worldServers[castedPacket.worldID].getBlockTileEntity(castedPacket.posX, castedPacket.posY, castedPacket.posZ);
			        if (entity != null){
			        	try{
			        		NBTTagCompound tag = new NBTTagCompound();
			        		entity.writeToNBT(tag);
			        		PacketDispatcher.sendPacketToPlayer(Packet0x02TENBTData.create(tag), player);
			        	}catch(Throwable e){
			        		WailaExceptionHandler.handleErr(e, entity.getClass().toString(), null);
			        	}
			        }
	        	}catch(Throwable e){
	        		WailaExceptionHandler.handleErr(e, "Error handling request packet 0x01.", null);	        		
	        	}
			}
			else if (header == 0x02){
				Packet0x02TENBTData castedPacket = new Packet0x02TENBTData(packet);
				DataAccessor.instance.remoteNbt = castedPacket.tag;
			}
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
