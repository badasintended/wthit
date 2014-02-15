package mcp.mobius.waila.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mcp.mobius.waila.utils.NBTUtil;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;

public class Packet0x04EntNBTData {
	public byte header;
	public NBTTagCompound tag;

	public Packet0x04EntNBTData(Packet250CustomPayload packet){
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		try{
			this.header  = inputStream.readByte();
			this.tag     = NBTUtil.readNBTTagCompound(inputStream);
		} catch (IOException e){}		
	}	
	
	public static Packet250CustomPayload create(NBTTagCompound tag){
		Packet250CustomPayload packet = new Packet250CustomPayload();
		ByteArrayOutputStream bos     = new ByteArrayOutputStream(1 + 4 + 4 + 4 + 4);
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try{
			outputStream.writeByte(0x04);
			NBTUtil.writeNBTTagCompound(tag, outputStream);
		}catch(IOException e){}
		
		packet.channel = "Waila";
		packet.data    = bos.toByteArray();
		packet.length  = bos.size();
		
		return packet;
	}	

 
    
}
