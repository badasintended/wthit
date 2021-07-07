package mcp.mobius.waila.config;

import mcp.mobius.waila.Waila;
import net.minecraft.util.Identifier;

@SuppressWarnings("ClassCanBeRecord")
public class Theme {

    public static final Theme VANILLA = new Theme(Waila.id("vanilla"), 0x100010, 0x5000ff, 0x28007f, 0xA0A0A0);
    public static final Theme DARK = new Theme(Waila.id("dark"), 0x131313, 0x383838, 0x242424, 0xA0A0A0);

    private final Identifier id;
    private final int backgroundColor;
    private final int gradientStart;
    private final int gradientEnd;
    private final int fontColor;

    public Theme(Identifier id, int backgroundColor, int gradientStart, int gradientEnd, int fontColor) {
        this.id = id;
        this.backgroundColor = backgroundColor;
        this.gradientStart = gradientStart;
        this.gradientEnd = gradientEnd;
        this.fontColor = fontColor;
    }

    public Identifier getId() {
        return id;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getGradientStart() {
        return gradientStart;
    }

    public int getGradientEnd() {
        return gradientEnd;
    }

    public int getFontColor() {
        return fontColor;
    }

}
