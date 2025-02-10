package mcp.mobius.waila.api.component;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IClientApiService;
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
        this(items, maxHeight, 1f);
    }

    public ItemListComponent(List<ItemStack> items, int maxHeight, float scale) {
        this.items = items;
        this.maxHeight = maxHeight;
        this.scale = scale;
    }

    private final List<ItemStack> items;
    private final int maxHeight;
    private final float scale;

    private int gridWidth;
    private int gridHeight;
    private int maxIndex;

    @Override
    public int getMinimalWidth() {
        return (int) (Math.min(items.size(), 9) * 18 * scale);
    }

    @Override
    public void setGrownWidth(int grownWidth) {
        gridWidth = Mth.ceil(grownWidth / (18 * scale));
        gridHeight = items.isEmpty() ? 0 : Math.min(Mth.positiveCeilDiv(items.size(), gridWidth), maxHeight);
        maxIndex = gridWidth * gridHeight - 1;
    }

    @Override
    public int getHeight() {
        return Mth.ceil(gridHeight * 18 * scale);
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        var pose = RenderSystem.getModelViewStack();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scale, scale, 1f);
        RenderSystem.applyModelViewMatrix();

        for (var i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var ix = (18 * (i % gridWidth)) + 1;
            var iy = (18 * (i / gridWidth)) + 1;
            IClientApiService.INSTANCE.renderItem(ix, iy, item);

            if (i == maxIndex) break;
        }

        pose.popPose();
        RenderSystem.applyModelViewMatrix();
    }

}
