package mcp.mobius.waila.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageReceiveData implements IMessage {

    public NBTTagCompound tag;

    public MessageReceiveData() {
    }

    public MessageReceiveData(NBTTagCompound tag) {
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            tag = CompressedStreamTools.readCompressed(new ByteBufInputStream(buf));
        } catch (Exception e) {
            tag = new NBTTagCompound();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, tag);
    }

    public static class Handler implements IMessageHandler<MessageReceiveData, IMessage> {

        @Override
        public IMessage onMessage(MessageReceiveData message, MessageContext ctx) {
            DataAccessorCommon.instance.setNBTData(message.tag);
            return null;
        }
    }
}
