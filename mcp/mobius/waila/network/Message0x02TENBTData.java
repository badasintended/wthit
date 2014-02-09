package mcp.mobius.waila.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.Packet;
import cpw.mods.fml.relauncher.Side;

public class Message0x02TENBTData extends SimpleChannelInboundHandler<Message0x02TENBTData> implements IWailaMessage {
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception {
		System.out.printf("ENCODING\n");
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage msg) {
		System.out.printf("Decode into 0x02\n");	
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message0x02TENBTData msg) throws Exception {
		System.out.printf("READING\n");
	}
}
