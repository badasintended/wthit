package mcp.mobius.waila.api;

import mcp.mobius.waila.overlay.DrawableText;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated use {@link IDrawableText}
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
public class RenderableTextComponent extends DrawableText {

    /**
     * @deprecated use {@link IDrawableText#of(Identifier, CompoundTag)}
     */
    @Deprecated
    public RenderableTextComponent(Identifier id, CompoundTag data) {
        super(id, data);
    }

    /**
     * @deprecated use {@link IDrawableText#of(IDrawableText...)}
     */
    @Deprecated
    public RenderableTextComponent(RenderableTextComponent... components) {
        super(components);
    }

}
