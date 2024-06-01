package mcp.mobius.waila.api.component;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Component that renders items with their names.
 */
@ApiSide.ClientOnly
public class NamedItemListComponent implements ITooltipComponent {

    private static final NamedItemComponent MORE = new NamedItemComponent(ItemStack.EMPTY.setHoverName(Component.literal("...")));;

    public NamedItemListComponent(List<ItemStack> items, int maxHeight) {
        this.items = items;
        this.maxHeight = maxHeight;
    }

    private final List<ItemStack> items;
    private final int maxHeight;

    private List<NamedItemComponent> getComponents() {
        List<NamedItemComponent> components = new ArrayList<>();

        for (var i = 0; i < items.size(); i++) {
            if (i >= maxHeight) break;
            components.add(new NamedItemComponent(items.get(i)));
        }

        if (items.size() > maxHeight) {
            components.add(MORE);
        }

        return components;
    }

    @Override
    public int getWidth() {
        var maxWidth = 0;
        for (var component : getComponents()) {
            maxWidth = Math.max(maxWidth, component.getWidth());
        }
        return maxWidth;
    }

    @Override
    public int getHeight() {
        return getComponents().size() * NamedItemComponent.HEIGHT;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, float delta) {
        for (var i = 0; i < getComponents().size(); i++) {
            var component = getComponents().get(i);
            var iy = y + (i * NamedItemComponent.HEIGHT);
            component.render(ctx, x, iy, delta);
        }
    }

}
