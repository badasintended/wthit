package mcp.mobius.waila.gui.hud;

import java.util.Map;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mcp.mobius.waila.api.IDrawableComponent;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.ITooltipLine;
import mcp.mobius.waila.hud.component.DrawableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

// TODO: remove ITaggableList
@SuppressWarnings("deprecation")
public class Tooltip extends ObjectArrayList<Component> implements ITooltip, ITaggableList<ResourceLocation, Component> {

    private final Object2IntMap<ResourceLocation> tags = new Object2IntOpenHashMap<>();

    public void setLine(ResourceLocation tag, Line line) {
        if (tags.containsKey(tag)) {
            set(tags.getInt(tag), line);
        } else {
            tags.put(tag, size);
            add(line);
        }
    }

    @Override
    public ITooltipLine addLine() {
        Line line = new Line(null);
        add(line);
        return line;
    }

    @Override
    public ITooltipLine setLine(ResourceLocation tag) {
        Line line = new Line(tag);
        setLine(tag, line);
        return line;
    }

    @Override
    public IDrawableComponent addDrawable() {
        DrawableComponent component = new DrawableComponent();
        addLine().with((ITooltipComponent) component);
        return component;
    }

    @Override
    public void setTag(ResourceLocation tag, Component value) {
        setLine(tag, value);
    }

    @Override
    public Component removeTag(ResourceLocation tag) {
        return tags.containsKey(tag)
            ? remove(tags.removeInt(tag))
            : null;
    }

    @Override
    public Component getTag(ResourceLocation tag) {
        return tags.containsKey(tag)
            ? get(tags.getInt(tag))
            : null;
    }

    @Override
    public Map<ResourceLocation, Component> getTags() {
        return tags
            .object2IntEntrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> get(e.getIntValue())));
    }

    @Override
    public void absorb(ITaggableList<ResourceLocation, Component> other) {
    }

    @Override
    public void clear() {
        super.clear();
        tags.clear();
    }

}
