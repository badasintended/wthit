package mcp.mobius.waila.network;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.AccessHelper;
import mcp.mobius.waila.utils.NBTUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Message0x01TERequest extends SimpleChannelInboundHandler<Message0x01TERequest> implements IWailaMessage {
	
	private static Field classToNameMap = null;
	
	static{
		try{
			classToNameMap = TileEntity.class.getDeclaredField("classToNameMap");
			classToNameMap.setAccessible(true);
		} catch (Exception e){
			
			try{
				classToNameMap = TileEntity.class.getDeclaredField("field_145853_j");
				classToNameMap.setAccessible(true);
			} catch (Exception f){
				throw new RuntimeException(f);
			}
			
		}
	}
	
	public int dim;
	public int posX;
	public int posY;
	public int posZ;
	public HashSet<String> keys = new HashSet<String> ();	
	
	public Message0x01TERequest(){}	
	
	public Message0x01TERequest(TileEntity ent, HashSet<String> keys){
		this.dim  = ent.getWorld().provider.getDimensionId();
		this.posX = ent.getPos().getX();
		this.posY = ent.getPos().getY();
		this.posZ = ent.getPos().getZ();
		this.keys = keys;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception {
		target.writeInt(dim);
		target.writeInt(posX);
		target.writeInt(posY);
		target.writeInt(posZ);
		target.writeInt(this.keys.size());
		
		for (String key : keys)
			WailaPacketHandler.INSTANCE.writeString(target, key);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage rawmsg) {
		try{
			Message0x01TERequest msg = (Message0x01TERequest)rawmsg;
			msg.dim  = dat.readInt();
			msg.posX = dat.readInt();
			msg.posY = dat.readInt();
			msg.posZ = dat.readInt();
			
			int nkeys = dat.readInt();
			
			for (int i = 0; i < nkeys; i++)
				this.keys.add(WailaPacketHandler.INSTANCE.readString(dat));
		
		}catch (Exception e){
			WailaExceptionHandler.handleErr(e, this.getClass().toString(), null);
		}		
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message0x01TERequest msg) throws Exception {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        World           world  = DimensionManager.getWorld(msg.dim);
		BlockPos		pos	   = new BlockPos(msg.posX, msg.posY, msg.posZ);
        TileEntity      entity = world.getTileEntity(pos);
        Block           block  = world.getBlockState(pos).getBlock();
        
        if (entity != null){
        	try{
        		NBTTagCompound tag  = new NBTTagCompound();
        		boolean hasNBTBlock = ModuleRegistrar.instance().hasNBTProviders(block);
        		boolean hasNBTEnt   = ModuleRegistrar.instance().hasNBTProviders(entity);

        		if (hasNBTBlock || hasNBTEnt){
        			tag.setInteger("x", msg.posX);
            		tag.setInteger("y", msg.posY);
            		tag.setInteger("z", msg.posZ);
            		tag.setString ("id", (String)((HashMap)classToNameMap.get(null)).get(entity.getClass()));
            		
            		EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
            		
            		for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getNBTProviders(block).values()){
	        			for (IWailaDataProvider provider : providersList){
	        				try{
	        					tag = provider.getNBTData(player, entity, tag, world, new BlockPos(msg.posX, msg.posY, msg.posZ));
	        				} catch (AbstractMethodError ame){
	        					tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);
	        				} catch (NoSuchMethodError nsm){
	        					tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);        					
	        				}
	        			}
            		}
        			
            		
            		for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getNBTProviders(entity).values()){
	        			for (IWailaDataProvider provider : providersList){
	        				try{        				
	        					tag = provider.getNBTData(player, entity, tag, world, new BlockPos(msg.posX, msg.posY, msg.posZ));
	        				} catch (AbstractMethodError ame){
	        					tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);
	        				} catch (NoSuchMethodError nsm){
	        					tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);        					
	        				}
	        			}
            		}
        			
        		} else {
        			entity.writeToNBT(tag);
        			tag = NBTUtil.createTag(tag, msg.keys);
        		}
        		
    			tag.setInteger("WailaX", msg.posX);
        		tag.setInteger("WailaY", msg.posY);
        		tag.setInteger("WailaZ", msg.posZ);
        		tag.setString ("WailaID", (String)((HashMap)classToNameMap.get(null)).get(entity.getClass()));

        		WailaPacketHandler.INSTANCE.sendTo(new Message0x02TENBTData(tag), WailaPacketHandler.getPlayer(ctx));
                //ctx.writeAndFlush(new Message0x02TENBTData(tag)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        	}catch(Throwable e){
        		WailaExceptionHandler.handleErr(e, entity.getClass().toString(), null);
        	}
        }
		
		
	}
}

