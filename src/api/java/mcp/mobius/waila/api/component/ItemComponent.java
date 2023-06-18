package mcp.mobius.waila.api.component;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * Component that renders an {@link ItemStack}.
 */
@ApiSide.ClientOnly
public class ItemComponent implements ITooltipComponent {

    public static final ItemComponent EMPTY = new ItemComponent(ItemStack.EMPTY);

    public ItemComponent(ItemStack stack) {
        this.stack = stack;
    }

    public ItemComponent(ItemLike item) {
        this(new ItemStack(item));
    }

    public final ItemStack stack;

    @Override
    public int getWidth() {
        return stack.isEmpty() ? 0 : 18;
    }

    @Override
    public int getHeight() {
        return stack.isEmpty() ? 0 : 18;
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        if (!stack.isEmpty()) {
            IApiService.INSTANCE.renderItem(matrices, x + 1, y + 1, stack);
        }
    }

}
