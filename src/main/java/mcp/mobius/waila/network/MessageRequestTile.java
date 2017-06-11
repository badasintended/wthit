package mcp.mobius.waila.network;

import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.NBTUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;
import java.util.Set;

public class MessageRequestTile implements IMessage {

    public int dim;
    public BlockPos pos;
    public Set<String> keys = Sets.newHashSet();

    public MessageRequestTile() {
    }

    public MessageRequestTile(TileEntity tile, Set<String> keys) {
        this.dim = tile.getWorld().provider.getDimension();
        this.pos = tile.getPos();
        this.keys = keys;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();
        pos = BlockPos.fromLong(buf.readLong());
        int nKeys = buf.readInt();
        for (int i = 0; i < nKeys; i++)
            keys.add(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dim);
        buf.writeLong(pos.toLong());
        buf.writeInt(keys.size());
        for (String key : keys)
            ByteBufUtils.writeUTF8String(buf, key);
    }

    public static class Handler implements IMessageHandler<MessageRequestTile, IMessage> {

        @Override
        public IMessage onMessage(final MessageRequestTile message, final MessageContext ctx) {
            final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() -> {
                World world = DimensionManager.getWorld(message.dim);
                if (!world.isBlockLoaded(message.pos))
                    return;

                TileEntity tile = world.getTileEntity(message.pos);
                IBlockState state = world.getBlockState(message.pos);
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;

                if (tile == null)
                    return;

                NBTTagCompound tag = new NBTTagCompound();
                boolean hasNBTBlock = ModuleRegistrar.instance().hasNBTProviders(state.getBlock());
                boolean hasNBTEnt = ModuleRegistrar.instance().hasNBTProviders(tile);

                if (hasNBTBlock || hasNBTEnt) {
                    tag.setInteger("x", message.pos.getX());
                    tag.setInteger("y", message.pos.getY());
                    tag.setInteger("z", message.pos.getZ());
                    tag.setString("id", TileEntity.getKey(tile.getClass()).toString());

                    for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getNBTProviders(state.getBlock()).values())
                        for (IWailaDataProvider provider : providersList)
                            tag = provider.getNBTData(player, tile, tag, world, message.pos);


                    for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getNBTProviders(tile).values())
                        for (IWailaDataProvider provider : providersList)
                            tag = provider.getNBTData(player, tile, tag, world, message.pos);
                } else {
                    tag = tile.getUpdateTag();
                    tag = NBTUtil.createTag(tag, message.keys);
                }

                tag.setInteger("WailaX", message.pos.getX());
                tag.setInteger("WailaY", message.pos.getY());
                tag.setInteger("WailaZ", message.pos.getZ());
                tag.setString("WailaID", TileEntity.getKey(tile.getClass()).toString());

                Waila.NETWORK_WRAPPER.sendTo(new MessageReceiveData(tag), player);
            });

            return null;
        }
    }
}
