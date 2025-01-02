package mcp.mobius.waila.api.util;

import net.minecraft.util.ARGB;

public final class WColors {

    public static double luminance(int rgb) {
        return (0.299 * ARGB.red(rgb) + 0.587 * ARGB.green(rgb) + 0.114 * ARGB.blue(rgb)) / 255.0;
    }

    private WColors() {
    }

}
