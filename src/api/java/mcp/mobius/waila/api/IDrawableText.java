package mcp.mobius.waila.api;

import java.awt.Dimension;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.impl.Impl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * @deprecated use {@link IDrawableComponent}
 */
@Deprecated
public interface IDrawableText extends MutableComponent {

    static IDrawableText of(ResourceLocation id, CompoundTag data) {
        return create().with(id, data);
    }

    static IDrawableText create() {
        return Impl.get(IDrawableText.class, 0);
    }

    IDrawableText with(ResourceLocation id, CompoundTag data);

    Dimension getSize();

    void render(PoseStack matrices, int x, int y, float delta);

}
