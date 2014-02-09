package mcp.mobius.waila.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class Message0x01TERequest extends SimpleChannelInboundHandler<IWailaMessage> implements IWailaMessage {
	
	public int dim;
	public int posX;
	public int posY;
	public int posZ;	
	
	public Message0x01TERequest(){}	
	
	public Message0x01TERequest(TileEntity ent){
		this.dim  = ent.getWorldObj().provider.dimensionId;
		this.posX = ent.xCoord;
		this.posY = ent.yCoord;
		this.posZ = ent.zCoord;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception {
		//System.out.printf("ENCODING %s\n", FMLCommonHandler.instance().getEffectiveSide());
		target.writeInt(dim);
		target.writeInt(posX);
		target.writeInt(posY);
		target.writeInt(posZ);		
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage rawmsg) {
		//System.out.printf("DECODING %s\n", FMLCommonHandler.instance().getEffectiveSide());
		Message0x01TERequest msg = (Message0x01TERequest)rawmsg;
		msg.dim  = dat.readInt();
		msg.posX = dat.readInt();
		msg.posY = dat.readInt();
		msg.posZ = dat.readInt();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IWailaMessage rawmsg) throws Exception {
		Message0x01TERequest msg = (Message0x01TERequest)rawmsg;
		System.out.printf("We are supposed to send back something here\n");
		System.out.printf("%s %s %s %s\n", msg.dim, msg.posX, msg.posY, msg.posZ);
	}
}
