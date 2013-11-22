package mcp.mobius.waila.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;

public class Packet0x02TENBTData {
	public byte header;
	public NBTTagCompound tag;

	public Packet0x02TENBTData(Packet250CustomPayload packet){
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		try{
			this.header  = inputStream.readByte();
			this.tag     = Packet0x02TENBTData.readNBTTagCompound(inputStream);
		} catch (IOException e){}		
	}	
	
	public static Packet250CustomPayload create(NBTTagCompound tag){
		Packet250CustomPayload packet = new Packet250CustomPayload();
		ByteArrayOutputStream bos     = new ByteArrayOutputStream(1 + 4 + 4 + 4 + 4);
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try{
			outputStream.writeByte(0x02);
			Packet0x02TENBTData.writeNBTTagCompound(tag, outputStream);
		}catch(IOException e){}
		
		packet.channel = "Waila";
		packet.data    = bos.toByteArray();
		packet.length  = bos.size();
		
		return packet;
	}	

    public static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, DataOutputStream par1DataOutputStream) throws IOException
    {
        if (par0NBTTagCompound == null)
        {
            par1DataOutputStream.writeShort(-1);
        }
        else
        {
            byte[] abyte = CompressedStreamTools.compress(par0NBTTagCompound);
            par1DataOutputStream.writeShort((short)abyte.length);
            par1DataOutputStream.write(abyte);
        }
    }	

    public static NBTTagCompound readNBTTagCompound(DataInputStream par0DataInputStream) throws IOException
    {
        short short1 = par0DataInputStream.readShort();

        if (short1 < 0)
        {
            return null;
        }
        else
        {
            byte[] abyte = new byte[short1];
            par0DataInputStream.readFully(abyte);
            return CompressedStreamTools.decompress(abyte);
        }
    }    
    
}
