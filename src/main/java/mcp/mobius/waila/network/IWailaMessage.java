package mcp.mobius.waila.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface IWailaMessage {
    public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception;

    public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage msg);
}
