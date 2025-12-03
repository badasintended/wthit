package mcp.mobius.waila.api.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.Nullable;

public final class WCodecs {

    @SuppressWarnings("NullableProblems")
    public static <B extends ByteBuf, V> StreamCodec<B, @Nullable V> nullable(StreamCodec<B, V> codec) {
        return new StreamCodec<>() {
            @Override
            public @Nullable V decode(B b) {
                return FriendlyByteBuf.readNullable(b, codec);
            }

            @Override
            public void encode(B o, V v) {
                FriendlyByteBuf.writeNullable(o, v, codec);
            }
        };
    }

    private WCodecs() {
    }

}
