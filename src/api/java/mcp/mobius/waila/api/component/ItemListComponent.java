package mcp.mobius.waila.api.component;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class ItemListComponent implements ITooltipComponent.HorizontalGrowing {

    public ItemListComponent(List<ItemStack> items) {
        this.items = items;
    }

    private final List<ItemStack> items;
    private int gridWidth;
    private int gridHeight;

    @Override
    public int getMinimalWidth() {
        return Math.min(items.size(), 9) * 18;
    }

    @Override
    public void setGrownWidth(int grownWidth) {
        gridWidth = grownWidth / 18;
        gridHeight = items.isEmpty() ? 0 : Mth.positiveCeilDiv(items.size(), gridWidth);
    }

    @Override
    public int getHeight() {
        return gridHeight * 18;
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            int ix = x + (18 * (i % gridWidth));
            int iy = y + (18 * (i / gridWidth));
            IApiService.INSTANCE.renderItem(matrices, ix, iy, item);
        }
    }

}
