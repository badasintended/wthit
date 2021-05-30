package mcp.mobius.waila.api;

import java.awt.Dimension;

import mcp.mobius.waila.api.internal.ApiSide;
import mcp.mobius.waila.overlay.DrawableText;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * A text that will be rendered as graphic instead.
 */
@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IDrawableText extends MutableText {

    /**
     * Create a drawable with this renderer.<br>
     * Shorthand for {@link #create()} and {@link #with(Identifier, NbtCompound)}
     */
    static IDrawableText of(Identifier id, NbtCompound data) {
        return new DrawableText().with(id, data);
    }

    /**
     * Create an empty drawable.
     */
    static IDrawableText create() {
        return new DrawableText();
    }

    /**
     * Add a new renderer to this drawable.
     *
     * @param id   the id associated with a {@link ITooltipRenderer}
     * @param data the data for that {@link ITooltipRenderer}
     */
    IDrawableText with(Identifier id, NbtCompound data);

    Dimension getSize();

    void render(MatrixStack matrices, int x, int y, float delta);

}
