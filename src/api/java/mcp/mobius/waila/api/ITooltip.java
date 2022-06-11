package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.component.PairComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface ITooltip {

    int getLineCount();

    /**
     * Returns the line on this index.
     * <p>
     * <b>Note:</b> {@link TooltipPosition position} dependant, you can't access
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

    /**
     * @deprecated use {@link ITooltipLine#with(Component)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.20")
    boolean add(Component component);

    /**
     * @deprecated use {@link ITooltipLine#with(ITooltipComponent)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.20")
    default void addPair(Component key, Component value) {
        addLine(new PairComponent(key, value));
    }

    /**
     * @deprecated use {@link ITooltipComponent}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.20")
    IDrawableComponent addDrawable();

    /**
     * @deprecated use {@link ITooltipComponent}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.20")
    default IDrawableComponent addDrawable(ResourceLocation id, CompoundTag data) {
        return addDrawable().with(id, data);
    }

    /**
     * @deprecated use {@link #setLine(ResourceLocation, Component)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.20")
    default void set(ResourceLocation tag, Component component) {
        setLine(tag, component);
    }

}
