package mcp.mobius.waila.api;

import java.util.List;

import mcp.mobius.waila.overlay.DrawableText;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public interface IDrawableText extends Text {

    static IDrawableText of(Identifier id, CompoundTag data) {
        return new DrawableText(id, data);
    }

    static IDrawableText of(IDrawableText... texts) {
        return new DrawableText(texts);
    }

    List<RenderContainer> getRenderers();

    interface RenderContainer {

        Identifier getId();

        CompoundTag getData();

        ITooltipRenderer getRenderer();

    }

}
