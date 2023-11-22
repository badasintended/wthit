package mcp.mobius.waila.api.component;

import java.util.List;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * Component that renders items that dynamically grow based on available space.
 */
@ApiSide.ClientOnly
public class ItemListComponent implements ITooltipComponent.HorizontalGrowing {

    public ItemListComponent(List<ItemStack> items) {
        this(items, 3);
    }

    public ItemListComponent(List<ItemStack> items, int maxHeight) {
        this.items = items;
        this.maxHeight = maxHeight;
    }

    private final List<ItemStack> items;
    private final int maxHeight;

    private int gridWidth;
    private int gridHeight;
    private int maxIndex;

    @Override
    public int getMinimalWidth() {
        return Math.min(items.size(), 9) * 18;
    }

    @Override
    public void setGrownWidth(int grownWidth) {
        gridWidth = grownWidth / 18;
        gridHeight = items.isEmpty() ? 0 : Math.min(Mth.positiveCeilDiv(items.size(), gridWidth), maxHeight);
        maxIndex = gridWidth * gridHeight - 1;
    }

    @Override
    public int getHeight() {
        return gridHeight * 18;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, float delta) {
        for (var i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var ix = x + (18 * (i % gridWidth)) + 1;
            var iy = y + (18 * (i / gridWidth)) + 1;
            ctx.renderItem(item, ix, iy);
            ItemComponent.renderItemDecorations(ctx, item, ix, iy);

            if (i == maxIndex) break;
        }
    }

}
