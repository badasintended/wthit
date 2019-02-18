package mcp.mobius.waila.api.impl;

import net.minecraft.text.StringTextComponent;
import net.minecraft.util.Identifier;

public class TaggedTextComponent extends StringTextComponent {

    private final Identifier tag;

    public TaggedTextComponent(Identifier tag) {
        super(String.format("${%s}", tag.toString()));

        this.tag = tag;
    }

    public Identifier getTag() {
        return tag;
    }
}
