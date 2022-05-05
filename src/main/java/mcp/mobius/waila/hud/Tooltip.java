package mcp.mobius.waila.hud;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipLine;
import net.minecraft.resources.ResourceLocation;

public class Tooltip extends ObjectArrayList<Line> implements ITooltip {

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

    public Line getTag(ResourceLocation tag) {
        return tags.containsKey(tag)
            ? get(tags.getInt(tag))
            : null;
    }

    @Override
    public void clear() {
        super.clear();
        tags.clear();
    }

}
