package mcp.mobius.waila.api.impl;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

public class TaggedTextComponent extends TextComponentString {

    private final ResourceLocation tag;

    public TaggedTextComponent(ResourceLocation tag) {
        super(String.format("${%s}", tag.toString()));

        this.tag = tag;
    }

    public ResourceLocation getTag() {
        return tag;
    }
}
