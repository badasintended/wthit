package mcp.mobius.waila.hud;

import java.util.Map;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.hud.component.PairComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

// TODO: remove ITaggableList
public class Tooltip extends ObjectArrayList<Component> implements ITooltip, ITaggableList<ResourceLocation, Component> {

    private final Object2IntMap<ResourceLocation> tags = new Object2IntOpenHashMap<>();

    @Override
    public boolean addPair(Component key, Component value) {
        return add(new PairComponent(key, value));
    }

    @Override
    public void set(ResourceLocation tag, Component component) {
        if (tags.containsKey(tag)) {
            set(tags.getInt(tag), component);
        } else {
            tags.put(tag, size);
            add(component);
        }
    }

    @Override
    @Nullable
    public Component remove(ResourceLocation tag) {
        return tags.containsKey(tag)
            ? remove(tags.removeInt(tag))
            : null;
    }

    @Override
    public Component get(ResourceLocation tag) {
        return tags.containsKey(tag)
            ? get(tags.getInt(tag))
            : null;
    }

    @Override
    public void setTag(ResourceLocation tag, Component value) {
        set(tag, value);
    }

    @Override
    public Component removeTag(ResourceLocation tag) {
        return remove(tag);
    }

    @Override
    public Component getTag(ResourceLocation tag) {
        return get(tag);
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
