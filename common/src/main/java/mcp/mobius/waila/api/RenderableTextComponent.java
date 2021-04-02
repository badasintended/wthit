package mcp.mobius.waila.api;

import mcp.mobius.waila.api.impl.DrawableText;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated use {@link IDrawableText}
 * TODO: Remove in 1.17 release
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
public class RenderableTextComponent extends DrawableText {

    /**
     * @deprecated use {@link IDrawableText#of(Identifier, CompoundTag)}
     */
    @Deprecated
    public RenderableTextComponent(Identifier id, CompoundTag data) {
        with(id, data);
    }

    @Deprecated
    public RenderableTextComponent(RenderableTextComponent... components) {
        for (RenderableTextComponent component : components) {
            for (RenderContainer renderer : component.renderers) {
                with(renderer.getId(), renderer.getData());
            }
        }
    }

}
