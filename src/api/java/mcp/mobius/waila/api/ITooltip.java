package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * The tooltip object that Waila will show.
 */
@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface ITooltip {

    int getLineCount();

    /**
     * Returns the line on this index.
     * <p>
     * <b>Note:</b> position dependant, you can't access
     * head tooltip in {@code appendBody}. To access all tooltip, use {@link IEventListener#onHandleTooltip}.
     */
    ITooltipLine getLine(int index);

    /**
     * Add a new line to the tooltip.
     */
    ITooltipLine addLine();

    /**
     * Replace the line that has the tag with a new line.
     *
     * @see WailaConstants
     */
    ITooltipLine setLine(ResourceLocation tag);

    /**
     * Returns the line with specified tag, if any.
     *
     * @see WailaConstants
     */
    @Nullable
    ITooltipLine getLine(ResourceLocation tag);

    /**
     * Add a new line to the tooltip.
     */
    default void addLine(Component component) {
        addLine().with(component);
    }

    /**
     * Add a new line to the tooltip.
     */
    default void addLine(ITooltipComponent component) {
        addLine().with(component);
    }

    /**
     * Replace the line that has the tag with a new line.
     *
     * @see WailaConstants
     */
    default void setLine(ResourceLocation tag, Component component) {
        setLine(tag).with(component);
    }

    /**
     * Replace the line that has the tag with a new line.
     *
     * @see WailaConstants
     */
    default void setLine(ResourceLocation tag, ITooltipComponent component) {
        setLine(tag).with(component);
    }

}
