package mcp.mobius.waila.api;

import java.text.DecimalFormat;

public final class WailaHelper {

    public static String suffix(long value) {
        if (value == Long.MIN_VALUE)
            return suffix(Long.MIN_VALUE + 1);
        if (value < 0)
            return "-" + suffix(-value);
        if (value < 1000)
            return Long.toString(value);

        int exp = (int) (Math.log(value) / Math.log(1000));
        return SUFFIXED_FORMAT.format(value / Math.pow(1000, exp)) + "KMGTPE".charAt(exp - 1);
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

    private static final DecimalFormat SUFFIXED_FORMAT = new DecimalFormat("0.##");

    private WailaHelper() {
        throw new UnsupportedOperationException();
    }

}
