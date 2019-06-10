package mcp.mobius.waila.api;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Identifier;

import java.util.List;

public class RenderableTextComponent extends TextComponent {

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
            ListTag list = data.getList("renders", NbtType.STRING);
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

        return renderers;
    }

    private CompoundTag getData() {
        try {
            return StringNbtReader.parse(getFormattedText());
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
        private final ITooltipRenderer renderer;

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

        public ITooltipRenderer getRenderer() {
            return renderer;
        }
    }
}
