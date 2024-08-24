package mcp.mobius.waila.api;

import java.awt.Rectangle;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Used to listen to generic Waila events.
 * <p>
 * Register implementations with {@link IClientRegistrar#eventListener}
 */
@ApiStatus.OverrideOnly
public interface IEventListener {

    /**
     * This event is fired just before the tooltip size is calculated.
     * This is the last chance to make edits to the information being displayed.
     */
    default void onHandleTooltip(ITooltip tooltip, ICommonAccessor accessor, IPluginConfig config) {
    }

    /**
     * This event is fired just before the tooltip is rendered.
     *
     * @param rect      the position and dimension of the tooltip, you can modify this to transform the tooltip
     * @param canceller call {@link Canceller#cancel()} to cancel this event, if canceled, the tooltip will not render
     */
    default void onBeforeTooltipRender(GuiGraphics ctx, Rectangle rect, ICommonAccessor accessor, IPluginConfig config, Canceller canceller) {
    }

    /**
     * This event is fired just after the tooltip is rendered.
     *
     * @param rect the position and dimension of the tooltip
     */
    default void onAfterTooltipRender(GuiGraphics ctx, Rectangle rect, ICommonAccessor accessor, IPluginConfig config) {
    }

    /**
     * This event is fired when item tooltip is displayed on container screen and waila showing the item's mod name.
     *
     * @return null if this listener doesn't decide the name, otherwise return a string
     */
    @Nullable
    default String getHoveredItemModName(ItemStack stack, IPluginConfig config) {
        return null;
    }

    interface Canceller {

        void cancel();

    }

}
