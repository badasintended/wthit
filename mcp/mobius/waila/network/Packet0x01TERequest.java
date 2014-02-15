package mcp.mobius.waila.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class Packet0x01TERequest {

	public byte header;
	public int worldID;
	public int posX;
	public int posY;
	public int posZ;
	public HashSet<String> keys = new HashSet<String> ();
	
	public Packet0x01TERequest(Packet250CustomPayload packet){
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		keys.clear();
		try{
			this.header  = inputStream.readByte();
			this.worldID = inputStream.readInt();
			this.posX    = inputStream.readInt();
			this.posY    = inputStream.readInt();
			this.posZ    = inputStream.readInt();
			
			int nkeys    = inputStream.readInt();
			for (int i = 0; i < nkeys; i++)
				keys.add(Packet.readString(inputStream, 250));
			
		} catch (IOException e){}		
	}	
	
	public static Packet250CustomPayload create(World world, MovingObjectPosition mop, HashSet<String> keys){
		Packet250CustomPayload packet = new Packet250CustomPayload();
		ByteArrayOutputStream bos     = new ByteArrayOutputStream(1 + 4 + 4 + 4 + 4);
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		keys.add("x"); keys.add("y"); keys.add("z");
		
		try{
			outputStream.writeByte(0x01);
			outputStream.writeInt(world.provider.dimensionId);
			outputStream.writeInt(mop.blockX);
			outputStream.writeInt(mop.blockY);
			outputStream.writeInt(mop.blockZ);
			outputStream.writeInt(keys.size());
			
			for (String key : keys)
				Packet.writeString(key, outputStream);
			
		}catch(IOException e){}
		
		packet.channel = "Waila";
		packet.data    = bos.toByteArray();
		packet.length  = bos.size();
		
		return packet;
	}
}
