package mcp.mobius.waila.api;

import java.awt.Dimension;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.internal.ApiSide;
import mcp.mobius.waila.impl.Impl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * A text that will be rendered as graphic instead.
 */
@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IDrawableText extends MutableComponent {

    /**
     * Create a drawable with this renderer.<br>
     * Shorthand for {@link #create()} and {@link #with(ResourceLocation, CompoundTag)}
     */
    static IDrawableText of(ResourceLocation id, CompoundTag data) {
        return create().with(id, data);
    }

    /**
     * Create an empty drawable.
     */
    static IDrawableText create() {
        return Impl.get(IDrawableText.class);
    }

    /**
     * Add a new renderer to this drawable.
     *
     * @param id   the id associated with a {@link ITooltipRenderer}
     * @param data the data for that {@link ITooltipRenderer}
     */
    IDrawableText with(ResourceLocation id, CompoundTag data);

    Dimension getSize();

    void render(PoseStack matrices, int x, int y, float delta);

}
