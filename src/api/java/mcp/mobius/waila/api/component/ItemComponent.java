package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.util.WNumbers;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        ctx.renderItem(stack, x + 1, y + 1);
        renderItemDecorations(ctx, stack, x + 1, y + 1);
    }

    static void renderItemDecorations(GuiGraphics ctx, ItemStack stack, int x, int y) {
        var client = Minecraft.getInstance();
        var count = stack.getCount();

        ctx.renderItemDecorations(client.font, stack, x + 1, y + 1, "");
        if (count <= 1) return;

        var countText = WNumbers.suffix(count);
        var actualW = client.font.width(countText);
        var scale = (actualW <= 16) ? 1f : 16f / actualW;

        var pose = ctx.pose();
        pose.pushMatrix();
        pose.scale(scale, scale);

        ctx.drawString(client.font, countText, (int) ((x + 17 - (actualW * scale)) / scale), (int) ((y + 17 - (client.font.lineHeight * scale)) / scale), 0xFFFFFFFF);
        pose.popMatrix();
    }

}
