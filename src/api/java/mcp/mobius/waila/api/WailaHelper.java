package mcp.mobius.waila.api;

import java.text.DecimalFormat;

public final class WailaHelper {

    public static String suffix(long value) {
        if (value == Long.MIN_VALUE) return suffix(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + suffix(-value);
        if (value < 1000) return Long.toString(value);

        var exponent = -1;
        long divisor = 0;

        for (var decimal : DECIMALS) {
            if (value < decimal) break;
            exponent++;
            divisor = decimal;
        }

        var truncated = (double) value / divisor;
        if (truncated >= 100) truncated = Math.round(truncated);
        else if (truncated >= 10) truncated = Math.round(truncated * 10) / 10d;

        return SUFFIXED_FORMAT.format(truncated) + "KMGTPE".charAt(exponent);
    }

    public static int getAlpha(int argb) {
        return (argb >> 24) & 0xFF;
    }

    public static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    public static int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    public static int getBlue(int rgb) {
        return rgb & 0xFF;
    }

    public static double getLuminance(int rgb) {
        return (0.299 * getRed(rgb) + 0.587 * getGreen(rgb) + 0.114 * getBlue(rgb)) / 255.0;
    }

    //---------------------------------------------------------------------------------------------------

    // @formatter:off
    private static final long[] DECIMALS = {
                            1_000L, // K
                        1_000_000L, // M
                    1_000_000_000L, // G
                1_000_000_000_000L, // T
            1_000_000_000_000_000L, // P
        1_000_000_000_000_000_000L  // E
    };
    // @formatter:on

    private static final DecimalFormat SUFFIXED_FORMAT = new DecimalFormat("0.##");

    private WailaHelper() {
        throw new UnsupportedOperationException();
    }

}
