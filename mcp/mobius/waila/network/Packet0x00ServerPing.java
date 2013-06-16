package mcp.mobius.waila.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.common.network.Player;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public class Packet0x00ServerPing extends Packet250CustomPayload {
	
	public byte header;
	
	public static Packet250CustomPayload create(){
		Packet250CustomPayload packet = new Packet250CustomPayload();
		ByteArrayOutputStream bos     = new ByteArrayOutputStream(1);
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try{
			outputStream.writeByte(0x00);
		}catch(IOException e){}
		
		packet.channel = "Waila";
		packet.data    = bos.toByteArray();
		packet.length  = bos.size();
		
		return packet;
	}
	
	public void read(){
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(this.data));
		try{
			this.header = inputStream.readByte();
		} catch (IOException e){}		
	}
}
