package mcp.mobius.waila.api;

import mcp.mobius.waila.api.internal.ApiSide;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface ITooltip {

    /**
     * Add a new component to the tooltip.
     */
    boolean add(Component component);

    /**
     * Add a key-value pair that it's value will get aligned with other pair.
     */
    void addPair(Component key, Component value);

    /**
     * Construct and add a graphical component.
     */
    IDrawableComponent addDrawable();

    /**
     * Construct and add a graphical component.
     * Shorthand for {@link #addDrawable()} and {@link IDrawableComponent#with(ResourceLocation, CompoundTag)}
     */
    IDrawableComponent addDrawable(ResourceLocation id, CompoundTag data);

    /**
     * Set the component with the tag.
     * Mainly used for overriding core components.
     *
     * @see WailaConstants
     */
    void set(ResourceLocation tag, Component component);

}
