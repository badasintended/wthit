package mcp.mobius.waila.api;

import io.netty.buffer.ByteBuf;
import mcp.mobius.waila.api.util.WCodecs;
import mcp.mobius.waila.api.util.WColors;
import mcp.mobius.waila.api.util.WNumbers;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

@Deprecated
public final class WailaHelper {

    @Deprecated
    public static String suffix(long value) {
        return WNumbers.suffix(value);
    }

    @Deprecated
    public static int getAlpha(int argb) {
        return ARGB.alpha(argb);
    }

    @Deprecated
    public static int getRed(int rgb) {
        return ARGB.red(rgb);
    }

    @Deprecated
    public static int getGreen(int rgb) {
        return ARGB.green(rgb);
    }

    @Deprecated
    public static int getBlue(int rgb) {
        return ARGB.blue(rgb);
    }

    @Deprecated
    public static double getLuminance(int rgb) {
        return WColors.luminance(rgb);
    }

    @Deprecated
    public static <B extends ByteBuf, V> StreamCodec<B, @Nullable V> nullable(final StreamCodec<B, V> codec) {
        return WCodecs.nullable(codec);
    }

    //---------------------------------------------------------------------------------------------------

    private WailaHelper() {
        throw new UnsupportedOperationException();
    }

}
