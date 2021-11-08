package mcp.mobius.waila.hud;

import java.util.Map;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mcp.mobius.waila.api.IDrawableComponent;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.hud.component.DrawableComponent;
import mcp.mobius.waila.hud.component.PairComponent;
import mcp.mobius.waila.hud.component.TaggedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

// TODO: remove ITaggableList
@SuppressWarnings("deprecation")
public class Tooltip extends ObjectArrayList<Component> implements ITooltip, ITaggableList<ResourceLocation, Component> {

    private final Object2IntMap<ResourceLocation> tags = new Object2IntOpenHashMap<>();

    @Override
    public void addPair(Component key, Component value) {
        add(new PairComponent(key, value));
    }

    @Override
    public IDrawableComponent addDrawable() {
        DrawableComponent component = new DrawableComponent();
        add(component);
        return component;
    }

    @Override
    public IDrawableComponent addDrawable(ResourceLocation id, CompoundTag data) {
        return addDrawable().with(id, data);
    }

    @Override
    public void set(ResourceLocation tag, Component component) {
        TaggedComponent tagged = new TaggedComponent(tag, component);
        if (tags.containsKey(tag)) {
            set(tags.getInt(tag), tagged);
        } else {
            tags.put(tag, size);
            add(tagged);
        }
    }

    @Override
    public void setTag(ResourceLocation tag, Component value) {
        set(tag, value);
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

    @Nullable
    public Component get(ResourceLocation tag) {
        return tags.containsKey(tag) ? ((TaggedComponent) get(tags.getInt(tag))).value : null;
    }

}
