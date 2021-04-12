package mcp.mobius.waila.util;

import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class TaggedText extends LiteralText {

    private final Identifier tag;

    public TaggedText(Identifier tag) {
        super(String.format("${%s}", tag.toString()));

        this.tag = tag;
    }

    public Identifier getTag() {
        return tag;
    }

}
