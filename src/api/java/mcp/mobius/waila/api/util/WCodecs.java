package mcp.mobius.waila.api.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WCodecs {

    public static <B extends ByteBuf, V> @NotNull StreamCodec<B, @Nullable V> nullable(StreamCodec<B, V> codec) {
        return new StreamCodec<>() {
            @Override
            @SuppressWarnings("NullableProblems")
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
