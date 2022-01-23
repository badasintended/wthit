package mcp.mobius.waila.api.component;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.world.item.ItemStack;

/**
 * A tooltip component that renders an {@link ItemStack}.
 */
@ApiSide.ClientOnly
public class ItemComponent implements ITooltipComponent {

    public ItemComponent(ItemStack stack) {
        this.stack = stack;
    }

    private final ItemStack stack;

    @Override
    public int getWidth() {
        return 18;
    }

    @Override
    public int getHeight() {
        return 19;
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        IApiService.INSTANCE.renderItem(x + 1, y + 1, stack);
    }

}
