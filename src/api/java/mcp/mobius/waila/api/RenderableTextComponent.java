package mcp.mobius.waila.api;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.sortme.JsonLikeTagParser;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.Identifier;

import java.util.List;

public class RenderableTextComponent extends StringTextComponent {

    public RenderableTextComponent(Identifier id, CompoundTag data) {
        super(getRenderString(id, data));
    }

    public RenderableTextComponent(RenderableTextComponent... components) {
        super(getRenderString(components));
    }

    public List<RenderContainer> getRenderers() {
        List<RenderContainer> renderers = Lists.newArrayList();
        CompoundTag data = getData();
        if (data.containsKey("renders")) {
            ListTag list = data.getList("renders", 10);
            list.forEach(t -> {
                CompoundTag tag = (CompoundTag) t;
                Identifier id = new Identifier(tag.getString("id"));
                CompoundTag dataTag = tag.getCompound("data");
                renderers.add(new RenderContainer(id, dataTag));
            });
        } else {
            Identifier id = new Identifier(data.getString("id"));
            CompoundTag dataTag = data.getCompound("data");
            renderers.add(new RenderContainer(id, dataTag));
        }

        return renderers;
    }

    private CompoundTag getData() {
        try {
            return JsonLikeTagParser.parse(getFormattedText());
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

    private static String getRenderString(RenderableTextComponent... components) {
        CompoundTag container = new CompoundTag();
        ListTag renderData = new ListTag();
        for (RenderableTextComponent component : components)
            renderData.add(new StringTag(component.getFormattedText()));
        container.put("renders", renderData);
        return container.toString();
    }

    public static class RenderContainer {
        private final Identifier id;
        private final CompoundTag data;
        private final IWailaTooltipRenderer renderer;

        public RenderContainer(Identifier id, CompoundTag data) {
            this.id = id;
            this.data = data;
            this.renderer = WailaRegistrar.INSTANCE.getTooltipRenderer(id);
        }

        public Identifier getId() {
            return id;
        }

        public CompoundTag getData() {
            return data;
        }

        public IWailaTooltipRenderer getRenderer() {
            return renderer;
        }
    }
}
