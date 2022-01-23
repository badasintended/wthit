package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * @see ITooltip#addDrawable
 */
@Deprecated
@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IDrawableComponent {

    /**
     * Add a new renderer to this component.
     *
     * @param id   the id associated with a {@link ITooltipRenderer}
     * @param data the data for that {@link ITooltipRenderer}
     */
    IDrawableComponent with(ResourceLocation id, CompoundTag data);

    /**
     * Add a traditional text-based component to this component.
     * <p>
     * <b>Note:</b> For more complex text, consider creating a custom {@link ITooltipRenderer} instead.
     */
    IDrawableComponent with(Component component);

}
