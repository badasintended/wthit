package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

/**
 * The base type of all Waila tooltip components.
 * <p>
 * See {@link mcp.mobius.waila.api.component} for default implementations.
 */
@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
public interface ITooltipComponent {

    /**
     * Returns the width of the component.
     * <p>
     * <b>Contract</b>: {@link #getWidth()} will be called first, then {@link #getHeight()}.
     */
    @SuppressWarnings("JavadocDeclaration")
    int getWidth();

    /**
     * Returns the height of the component.
     * <p>
     * <b>Contract</b>: {@link #getWidth()} will be called first, then {@link #getHeight()}.
     */
    @SuppressWarnings("JavadocDeclaration")
    int getHeight();

    /**
     * Renders the component.
     */
    void render(GuiGraphics ctx, int x, int y, DeltaTracker delta);

    /**
     * A component that will grow in size relative to overall tooltip width.
     * <p>
     * The size in which the component will grow is calculated by dividing
     * the available space with the total growing component {@linkplain #getWeight() weight}.
     */
    @ApiSide.ClientOnly
    @ApiStatus.OverrideOnly
    interface HorizontalGrowing extends ITooltipComponent {

        /**
         * Returns the minimal width of the component.
         */
        int getMinimalWidth();

        /**
         * Called with the calculated width.
         */
        void setGrownWidth(int grownWidth);

        /**
         * Returns the height of the component.
         * <p>
         * <b>Contract</b>: {@link #setGrownWidth(int)} will be called first, then {@link #getHeight()}.
         */
        @Override
        @SuppressWarnings("JavadocDeclaration")
        int getHeight();

        /**
         * Returns how much the component will grow relative to the rest of the growing components.
         * <p>
         * Should be a final value that will not change.
         */
        default int getWeight() {
            return 1;
        }

        /**
         * @deprecated width is dynamically calculated.
         */
        @Override
        @Deprecated
        @ApiStatus.NonExtendable
        default int getWidth() {
            return getMinimalWidth();
        }

    }

}
