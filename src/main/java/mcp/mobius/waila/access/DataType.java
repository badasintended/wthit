package mcp.mobius.waila.access;

import io.netty.handler.codec.EncoderException;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record DataType<D extends IData>(ResourceLocation id) implements IData.Type<D> {

    public static final StreamCodec<RegistryFriendlyByteBuf, IData> CODEC = new StreamCodec<>() {
        @Override
        public IData decode(RegistryFriendlyByteBuf buf) {
            var id = buf.readResourceLocation();
            var codec = Registrar.get().dataCodecs.get(id);
            if (codec == null) {
                throw new EncoderException("[%s] Received unknown data type [%s]".formatted(WailaConstants.MOD_NAME, id));
            }

            try {
                return codec.decode(buf);
            } catch (Exception e) {
                throw new EncoderException("[%s] Failed to decode data [%s]".formatted(WailaConstants.MOD_NAME, id), e);
            }
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, IData data) {
            var id = data.type().id();
            var codec = Registrar.get().dataCodecs.get(id);
            if (codec == null) {
                throw new EncoderException("[%s] Unknown data type [%s]".formatted(WailaConstants.MOD_NAME, data.type().id()));
            }

            try {
                buf.writeResourceLocation(id);
                codec.encode(buf, data);
            } catch (Exception e) {
                throw new EncoderException("[%s] Failed to encode data [%s]".formatted(WailaConstants.MOD_NAME, data.type().id()), e);
            }
        }
    };

}
