package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Range;

/**
 * A theme that will be used to render the Waila tooltip.
 */
@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
@ApiStatus.Experimental
public interface ITheme {

    /**
     * This method is called when properties are changed in game.
     * When a property is modified in this method, the editor input field for that property will be changed accordingly.
     * This is useful for validating property values.
     *
     * @param accessor the context accessor
     */
    default void processProperties(IThemeAccessor accessor) {
    }

    /**
     * Returns the default text color of this theme.
     */
    int getDefaultTextColor();

    /**
     * Sets the padding of this theme, {@link Padding#set} overloads are based on CSS shorthand padding property.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding#syntax">CSS Padding entry on MDN Web Docs</a>
     */
    void setPadding(Padding padding);

    /**
     * Render the tooltip background. <b>Padding is already counted in the specified hud dimension.</b>
     *
     * @param ctx    the draw context
     * @param x      the x position of the hud
     * @param y      the y position of the hud
     * @param width  the width of the hud
     * @param height the height of the hud
     * @param alpha  the background transparancy of the hud, ranged from {@code 0} to {@code 255}
     * @param delta  frame time delta
     */
    void renderTooltipBackground(GuiGraphics ctx, int x, int y, int width, int height, @Range(from = 0x00, to = 0xFF) int alpha, DeltaTracker delta);

    @ApiSide.ClientOnly
    @ApiStatus.Experimental
    @ApiStatus.NonExtendable
    interface Padding {

        void set(int all);

        void set(int topBottom, int leftRight);

        void set(int top, int leftRight, int bottom);

        void set(int top, int right, int bottom, int left);

    }

}
