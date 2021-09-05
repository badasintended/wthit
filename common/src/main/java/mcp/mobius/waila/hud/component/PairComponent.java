package mcp.mobius.waila.hud.component;

import java.util.Collections;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public record PairComponent(Component key, Component value) implements MutableComponent {

    @Override
    public Style getStyle() {
        return Style.EMPTY;
    }

    @Override
    public String getContents() {
        return key.getContents() + ": " + value.getContents();
    }

    @Override
    public List<Component> getSiblings() {
        return Collections.emptyList();
    }

    @Override
    public MutableComponent plainCopy() {
        return new PairComponent(key.plainCopy(), value.plainCopy());
    }

    @Override
    public MutableComponent copy() {
        return new PairComponent(key.copy(), value.copy());
    }

    @Override
    public FormattedCharSequence getVisualOrderText() {
        return FormattedCharSequence.EMPTY;
    }

    @Override
    public MutableComponent setStyle(Style style) {
        return this;
    }

    @Override
    public MutableComponent append(Component component) {
        return this;
    }

}
