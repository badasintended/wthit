package mcp.mobius.waila.network;

import cpw.mods.fml.relauncher.Side;
import mcp.mobius.waila.Waila;
import net.minecraft.network.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class Message0x00ServerPing extends SimpleChannelInboundHandler<IWailaMessage> implements IWailaMessage {

	@Override
	public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception {
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage msg) {
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IWailaMessage msg) throws Exception {
		Waila.log.info("Received server authentication msg. Remote sync will be activated");
		Waila.instance.serverPresent = true;		
	}
}
