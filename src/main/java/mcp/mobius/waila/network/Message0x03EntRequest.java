package mcp.mobius.waila.network;

import java.util.HashSet;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.AccessHelper;
import mcp.mobius.waila.utils.NBTUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class Message0x03EntRequest extends SimpleChannelInboundHandler<Message0x03EntRequest> implements IWailaMessage {

	public int dim;
	public int id;
	public HashSet<String> keys = new HashSet<String> ();	
	
	public Message0x03EntRequest(){}	
	
	public Message0x03EntRequest(Entity ent, HashSet<String> keys){
		this.dim  = ent.worldObj.provider.dimensionId;
		this.id   = ent.getEntityId();
		this.keys = keys;
	}	
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception {
		target.writeInt(dim);
		target.writeInt(id);
		target.writeInt(this.keys.size());
		
		for (String key : keys)
			WailaPacketHandler.INSTANCE.writeString(target, key);		
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage rawmsg) {
		try{
			Message0x03EntRequest msg = (Message0x03EntRequest)rawmsg;
			msg.dim  = dat.readInt();
			msg.id   = dat.readInt();

			
			int nkeys = dat.readInt();
			
			for (int i = 0; i < nkeys; i++)
				this.keys.add(WailaPacketHandler.INSTANCE.readString(dat));
		
		}catch (Exception e){
			WailaExceptionHandler.handleErr(e, this.getClass().toString(), null);
		}		
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message0x03EntRequest msg) throws Exception {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        World           world  = DimensionManager.getWorld(msg.dim);
        Entity          entity = world.getEntityByID(msg.id);
        
        if (entity != null){
        	try{
        		NBTTagCompound tag = new NBTTagCompound();
        		
        		EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
        		
        		if (ModuleRegistrar.instance().hasNBTEntityProviders(entity)){
        			for (List<IWailaEntityProvider> providersList : ModuleRegistrar.instance().getNBTEntityProviders(entity).values()){
	        			for (IWailaEntityProvider provider : providersList){
	        				try{
	        					tag = provider.getNBTData(player, entity, tag, world);
	        				} catch (AbstractMethodError ame){
	        					tag = AccessHelper.getNBTData(provider, entity, tag);
	        				}        				
	        			}
        			}

        		} else {
            		entity.writeToNBT(tag);
            		tag = NBTUtil.createTag(tag, msg.keys);
        		}
        		
        		tag.setInteger("WailaEntityID", entity.getEntityId());

        		ctx.writeAndFlush(new Message0x04EntNBTData(tag)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);        		
        	}catch(Throwable e){
        		WailaExceptionHandler.handleErr(e, entity.getClass().toString(), null);
        	}
        }		
	}

}
