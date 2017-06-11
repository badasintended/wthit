package mcp.mobius.waila.network;

import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.NBTUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MessageRequestEntity implements IMessage {

    public int dim;
    public int entityId;
    public Set<String> keys = Sets.newHashSet();

    public MessageRequestEntity() {
    }

    public MessageRequestEntity(Entity entity, Set<String> keys) {
        this.dim = entity.getEntityWorld().provider.getDimension();
        this.entityId = entity.getEntityId();
        this.keys = keys;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();
        entityId = buf.readInt();
        int nKeys = buf.readInt();
        for (int i = 0; i < nKeys; i++)
            keys.add(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dim);
        buf.writeInt(entityId);
        buf.writeInt(keys.size());
        for (String key : keys)
            ByteBufUtils.writeUTF8String(buf, key);
    }

    public static class Handler implements IMessageHandler<MessageRequestEntity, IMessage> {

        @Override
        public IMessage onMessage(final MessageRequestEntity message, final MessageContext ctx) {
            final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() -> {
                World world = DimensionManager.getWorld(message.dim);
                Entity entity = world.getEntityByID(message.entityId);
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;

                if (entity == null)
                    return;

                NBTTagCompound tag = new NBTTagCompound();

                if (ModuleRegistrar.instance().hasNBTEntityProviders(entity)) {
                    for (List<IWailaEntityProvider> providersList : ModuleRegistrar.instance().getNBTEntityProviders(entity).values())
                        for (IWailaEntityProvider provider : providersList)
                            tag = provider.getNBTData(player, entity, tag, world);
                } else {
                    entity.writeToNBT(tag);
                    tag = NBTUtil.createTag(tag, message.keys);
                }

                tag.setInteger("WailaEntityID", entity.getEntityId());

                Waila.NETWORK_WRAPPER.sendTo(new MessageReceiveData(tag), player);
            });

            return null;
        }
    }
}
