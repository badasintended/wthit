package mcp.mobius.waila.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;

import java.io.IOException;
import java.util.EnumMap;

public enum WailaPacketHandler {
	INSTANCE;
	
    public EnumMap<Side, FMLEmbeddedChannel> channels;	
    
    private WailaPacketHandler(){
        this.channels = NetworkRegistry.INSTANCE.newChannel("Waila", new WailaCodec());
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT){
            addClientHandlers();
        	addServerHandlers();
        }else{
        	addServerHandlers();
        }
       
    }

    private void addClientHandlers(){
        FMLEmbeddedChannel channel = this.channels.get(Side.CLIENT);
        String codec = channel.findChannelHandlerNameForType(WailaCodec.class);
        
        channel.pipeline().addAfter(codec,        "ServerPing", new Message0x00ServerPing());
        channel.pipeline().addAfter("ServerPing", "TENBTData",  new Message0x02TENBTData());
        channel.pipeline().addAfter("TENBTData",  "EntNBTData", new Message0x04EntNBTData()); 
    }
    
    private void addServerHandlers(){
        FMLEmbeddedChannel channel = this.channels.get(Side.SERVER);
        String codec = channel.findChannelHandlerNameForType(WailaCodec.class);
        
        channel.pipeline().addAfter(codec,       "TERequest",  new Message0x01TERequest());
        channel.pipeline().addAfter("TERequest", "EntRequest", new Message0x03EntRequest());
    }    
    
    private class WailaCodec extends FMLIndexedMessageToMessageCodec<IWailaMessage>
    {
        public WailaCodec()
        {
            addDiscriminator(0, Message0x00ServerPing.class);
            addDiscriminator(1, Message0x01TERequest.class);
            addDiscriminator(2, Message0x02TENBTData.class);
            addDiscriminator(3, Message0x03EntRequest.class);
            addDiscriminator(4, Message0x04EntNBTData.class);
        }
        
        @Override
        public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception
        {
        	msg.encodeInto(ctx, msg, target);
        }

        @Override
        public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage msg)
        {
        	msg.decodeInto(ctx, dat, msg);
        }
   }
    
    public void sendTo(IWailaMessage message, EntityPlayerMP player){
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);    	
    }
    
    public void sendToServer(IWailaMessage message){
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
    
    public void writeNBT(ByteBuf target, NBTTagCompound tag) throws IOException{
        if (tag == null)
        	target.writeShort(-1);
        else{
            byte[] abyte = CompressedStreamTools.compress(tag);
            target.writeShort((short)abyte.length);
            target.writeBytes(abyte);
        }
    }

    public NBTTagCompound readNBT(ByteBuf dat) throws IOException
    {
        short short1 = dat.readShort();

        if (short1 < 0)
            return null;
        else{
            byte[] abyte = new byte[short1];
            dat.readBytes(abyte);
            return CompressedStreamTools.func_152457_a(abyte, NBTSizeTracker.field_152451_a);
        }
    }
    
    public void writeString(ByteBuf buffer, String data) throws IOException
    {
        byte[] abyte = data.getBytes(Charsets.UTF_8);
       	buffer.writeShort(abyte.length);
       	buffer.writeBytes(abyte);
    }
    
    public String readString(ByteBuf buffer) throws IOException
    {
        int j = buffer.readShort();
        String s = new String(buffer.readBytes(j).array(), Charsets.UTF_8);
        return s;
    }

	public static EntityPlayerMP getPlayer(ChannelHandlerContext ctx){
		return ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
	}
}
