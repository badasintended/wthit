package mcp.mobius.waila.api.component;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/**
 * Component that renders items with their names.
 */
@ApiSide.ClientOnly
public class NamedItemListComponent implements ITooltipComponent {

    private static final String MORE = "...";

    public NamedItemListComponent(List<ItemStack> items, int maxHeight) {
        this.components = new ArrayList<>(Math.min(items.size(), maxHeight));
        for (var i = 0; i < items.size(); i++) {
            if (i >= maxHeight) break;
            this.components.add(new NamedItemComponent(items.get(i)));
        }

        this.hasOverflow = items.size() > maxHeight;
    }

    private final List<NamedItemComponent> components;
    private final boolean hasOverflow;

    @Override
    public int getWidth() {
        var maxWidth = 0;
        for (var component : components) {
            maxWidth = Math.max(maxWidth, component.getWidth());
        }
        if (hasOverflow) {
            maxWidth = Math.max(maxWidth, getFont().width(MORE) + 10);
        }
        return maxWidth;
    }

    @Override
    public int getHeight() {
        var height = 0;
        for (var component : components) {
            height += component.getHeight();
        }
        if (hasOverflow) {
            height += getFont().lineHeight;
        }
        return height;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        var iy = y;

        for (var component : components) {
            component.render(ctx, x, iy, delta);
            iy += component.getHeight();
        }

        if (hasOverflow) {
            ctx.drawString(getFont(), MORE, x + 10, iy, IApiService.INSTANCE.getFontColor());
        }
    }

    private Font getFont() {
        return Minecraft.getInstance().font;
    }

}
