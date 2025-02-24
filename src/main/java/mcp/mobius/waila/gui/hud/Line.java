package mcp.mobius.waila.gui.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.ITooltipComponent.HorizontalGrowing;
import mcp.mobius.waila.api.ITooltipLine;
import mcp.mobius.waila.api.component.WrappedComponent;
import mcp.mobius.waila.registry.PluginAware;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class Line implements ITooltipLine {

    public final @Nullable ResourceLocation tag;
    public final List<ITooltipComponent> components = new ArrayList<>();
    public final Object2IntOpenHashMap<ITooltipComponent> widths = new Object2IntOpenHashMap<>();
    public final Object2IntMap<ITooltipComponent> heights = new Object2IntOpenHashMap<>();

    public @Nullable PluginAware<?> origin;

    private int fixedWidth = -1;
    private int width = -1;
    private int height = -1;

    private int growingWeight = 0;
    private int growingMinWidth = 0;

    public Line(@Nullable ResourceLocation tag) {
        this.tag = tag;
    }

    @Override
    public Line with(ITooltipComponent component) {
        component = InspectComponent.maybeWrap(component, origin, tag);
        components.add(component);
        if (component instanceof HorizontalGrowing growing) {
            growingWeight += growing.getWeight();
        }
        return this;
    }

    @Override
    public Line with(Component component) {
        return with(new WrappedComponent(component));
    }

    public void calculateFixedWidth() {
        if (fixedWidth != -1) return;

        fixedWidth = components.stream().mapToInt(c -> {
            var width = c.getWidth();
            if (c instanceof HorizontalGrowing) growingMinWidth += width;
            widths.put(c, width);
            return width;
        }).sum();
    }

    // TODO: Figure out how to calculate gaps for growing component.
    //       But in the end it doesn't really matter.
    public void calculateDynamicWidth(int maxWidth) {
        if (width != -1) return;

        if (growingWeight > 0) {
            var fixedWidth = this.fixedWidth - growingMinWidth;
            var unfrozenWeight = growingWeight;

            List<HorizontalGrowing> calculate = components.stream()
                .filter(it -> it instanceof HorizontalGrowing)
                .map(it -> (HorizontalGrowing) it)
                .collect(Collectors.toCollection(ArrayList::new));

            while (!calculate.isEmpty()) {
                var growingWidth = -1f;
                var success = true;

                var iterator = calculate.iterator();
                while (iterator.hasNext()) {
                    var growing = iterator.next();
                    if (growingWidth == -1) {
                        growingWidth = (float) (maxWidth - fixedWidth) / unfrozenWeight;
                    }

                    var weightedWidth = (int) (growingWidth * growing.getWeight());
                    int newWidth;
                    if (weightedWidth > growing.getMinimalWidth()) {
                        newWidth = weightedWidth;
                    } else {
                        newWidth = growing.getMinimalWidth();
                        fixedWidth += newWidth + 1;
                        unfrozenWeight -= growing.getWeight();
                        iterator.remove();
                        success = false;
                    }

                    growing.setGrownWidth(newWidth);
                    widths.put(growing, newWidth);
                }

                if (success) break;
            }
        }

        width = components.stream().mapToInt(c -> {
            var width = widths.getInt(c);
            return width > 0 ? width + 1 : 0;
        }).sum();

        if (width > 0) width--;
    }

    public void calculateHeight() {
        if (height != -1) return;

        height = components.stream().mapToInt(c -> {
            var height = c.getHeight();
            heights.put(c, height);
            return height;
        }).max().orElse(0);
    }

    public int getFixedWidth() {
        Preconditions.checkState(fixedWidth != -1);
        return fixedWidth;
    }

    public int getWidth() {
        Preconditions.checkState(width != -1);
        return width;
    }

    public int getHeight() {
        Preconditions.checkState(height != -1);
        return height;
    }

    public void render(ComponentRenderer renderer, GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        Preconditions.checkState(width != -1 && height != -1);

        var cx = x;
        for (var component : components) {
            var w = widths.getInt(component);
            if (w <= 0) continue;
            var h = heights.getInt(component);

            var cy = y + (h < height ? (height - h) / 2 : 0);
            renderer.render(ctx, component, cx, cy, w, h, delta);
            cx += w + 1;
        }
    }

}
