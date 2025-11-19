package mcp.mobius.waila.gui.hud;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipLine;
import mcp.mobius.waila.registry.PluginAware;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class Tooltip extends ObjectArrayList<Line> implements ITooltip {

    private final Object2IntMap<Identifier> tags = new Object2IntOpenHashMap<>();
    public @Nullable PluginAware<?> origin;

    public void setLine(Identifier tag, Line line) {
        if (tags.containsKey(tag)) {
            set(tags.getInt(tag), line);
        } else {
            tags.put(tag, size);
            add(line);
        }
    }

    @Override
    public int getLineCount() {
        return size;
    }

    @Override
    public ITooltipLine getLine(int index) {
        var line = get(index);
        line.origin = origin;
        return line;
    }

    @Override
    public ITooltipLine addLine() {
        var line = new Line(null);
        line.origin = origin;
        add(line);
        return line;
    }

    @Override
    public ITooltipLine setLine(Identifier tag) {
        var line = new Line(tag);
        line.origin = origin;
        setLine(tag, line);
        return line;
    }

    @Override
    public Line getLine(Identifier tag) {
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
