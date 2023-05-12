package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * A tooltip component that renders an {@link ItemStack}.
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
    public void render(GuiGraphics ctx, int x, int y, float delta) {
        ctx.renderItem(stack, x + 1, y + 1);
        ctx.renderItemDecorations(Minecraft.getInstance().font, stack, x + 1, y + 1);
    }

}
