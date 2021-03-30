package mcp.mobius.waila.overlay;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.ITooltipRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class DrawableText extends LiteralText implements IDrawableText {

    private List<IDrawableText.RenderContainer> renderers = null;

    public DrawableText(Identifier id, CompoundTag data) {
        super(getRenderString(id, data));
    }

    public DrawableText(IDrawableText... components) {
        super(getRenderString(components));
    }

    @Override
    public List<IDrawableText.RenderContainer> getRenderers() {
        if (renderers == null) {
            renderers = Lists.newArrayList();
            CompoundTag data = getData();
            if (data.contains("renders")) {
                ListTag list = data.getList("renders", 8 /* STRING */);
                list.forEach(t -> {
                    StringTag stringTag = (StringTag) t;
                    try {
                        CompoundTag tag = StringNbtReader.parse(stringTag.asString());
                        Identifier id = new Identifier(tag.getString("id"));
                        CompoundTag dataTag = tag.getCompound("data");
                        renderers.add(new RenderContainer(id, dataTag));
                    } catch (CommandSyntaxException e) {
                        // no-op
                    }
                });
            } else {
                Identifier id = new Identifier(data.getString("id"));
                CompoundTag dataTag = data.getCompound("data");
                renderers.add(new RenderContainer(id, dataTag));
            }
        }
        return renderers;
    }

    private CompoundTag getData() {
        try {
            return StringNbtReader.parse(getString());
        } catch (CommandSyntaxException e) {
            return new CompoundTag();
        }
    }

    private static String getRenderString(Identifier id, CompoundTag data) {
        CompoundTag renderData = new CompoundTag();
        renderData.putString("id", id.toString());
        renderData.put("data", data);
        return renderData.toString();
    }

    private static String getRenderString(IDrawableText... components) {
        CompoundTag container = new CompoundTag();
        ListTag renderData = new ListTag();
        for (IDrawableText component : components)
            renderData.add(StringTag.of(component.getString()));
        container.put("renders", renderData);
        return container.toString();
    }

    public static class RenderContainer implements IDrawableText.RenderContainer {

        private final Identifier id;
        private final CompoundTag data;
        private final ITooltipRenderer renderer;

        public RenderContainer(Identifier id, CompoundTag data) {
            this.id = id;
            this.data = data;
            this.renderer = Registrar.INSTANCE.getRenderer(id);
        }

        @Override
        public Identifier getId() {
            return id;
        }

        @Override
        public CompoundTag getData() {
            return data;
        }

        @Override
        public ITooltipRenderer getRenderer() {
            return renderer;
        }

    }

}
