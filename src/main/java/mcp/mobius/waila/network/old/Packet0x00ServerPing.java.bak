package mcp.mobius.waila.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.config.ConfigCategory;

public class Packet0x00ServerPing {
	
	public byte header;
	public HashMap<String, Boolean> forcedKeys = new HashMap<String, Boolean>();
	
	public Packet0x00ServerPing(Packet250CustomPayload packet){
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		try{
			this.header = inputStream.readByte();

			forcedKeys.clear();
			while (true){
				String key = Packet.readString(inputStream, 255);
				if (key.equals("END OF LIST")) break;
				boolean value = inputStream.readBoolean();
				forcedKeys.put(key, value);
			}
			
		} catch (IOException e){}		
	}	
	
	public static Packet250CustomPayload create(){
		Packet250CustomPayload packet = new Packet250CustomPayload();
		ByteArrayOutputStream bos     = new ByteArrayOutputStream(1);
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try{
			outputStream.writeByte(0x00);
			
			ConfigCategory serverForcing = ConfigHandler.instance().config.getCategory(Constants.CATEGORY_SERVER);
			
			/*
			ConfigCategory serverModules = ConfigHandler.instance().config.getCategory(Constants.CATEGORY_MODULES);
			
			HashMap<String, Boolean> keys = new HashMap<String, Boolean>();
			
			for (String key : serverModules.keySet()){
				keys.put(key, serverModules.get(key).getBoolean(true));
			}
			*/
			
			for (String key : serverForcing.keySet()){
				if (serverForcing.get(key).getBoolean(false)){
					Packet.writeString(key, outputStream);
					outputStream.writeBoolean(ConfigHandler.instance().getConfig(key));
				}
			}
			
			Packet.writeString("END OF LIST", outputStream);
			
		}catch(IOException e){}
		
		packet.channel = "Waila";
		packet.data    = bos.toByteArray();
		packet.length  = bos.size();
		
		return packet;
	}
	

}
