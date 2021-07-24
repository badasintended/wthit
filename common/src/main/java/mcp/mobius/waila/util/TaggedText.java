package mcp.mobius.waila.util;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class TaggedText extends TextComponent {

    private final ResourceLocation tag;

    public TaggedText(ResourceLocation tag) {
        super(String.format("${%s}", tag.toString()));

        this.tag = tag;
    }

    public ResourceLocation getTag() {
        return tag;
    }

}
