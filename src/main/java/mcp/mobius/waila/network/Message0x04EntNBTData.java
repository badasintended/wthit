package mcp.mobius.waila.network;

import mcp.mobius.waila.api.impl.DataAccessorEntity;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class Message0x04EntNBTData extends SimpleChannelInboundHandler<Message0x04EntNBTData> implements IWailaMessage {

	NBTTagCompound tag;
	
	public Message0x04EntNBTData(){}	
	
	public Message0x04EntNBTData(NBTTagCompound tag){
		this.tag = tag;
	}		
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception {
		WailaPacketHandler.INSTANCE.writeNBT(target, tag);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage msg) {
		try{
			this.tag = WailaPacketHandler.INSTANCE.readNBT(dat);
		} catch (Exception e){
    		WailaExceptionHandler.handleErr(e, this.getClass().toString(), null);
		}		
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message0x04EntNBTData msg) throws Exception {
		DataAccessorEntity.instance.remoteNbt = msg.tag;
	}

}
