package mcp.mobius.waila.hud.component;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public final class TaggedComponent implements MutableComponent {

    public final ResourceLocation tag;
    public final Component value;

    public TaggedComponent(ResourceLocation tag, Component value) {
        this.tag = tag;
        this.value = value;
    }

    @Override
    public MutableComponent setStyle(Style style) {
        return this;
    }

    @Override
    public MutableComponent append(Component component) {
        return this;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY;
    }

    @Override
    public String getContents() {
        return "";
    }

    @Override
    public List<Component> getSiblings() {
        return ObjectLists.emptyList();
    }

    @Override
    public MutableComponent plainCopy() {
        return this;
    }

    @Override
    public MutableComponent copy() {
        return this;
    }

    @Override
    public FormattedCharSequence getVisualOrderText() {
        return FormattedCharSequence.EMPTY;
    }

}
