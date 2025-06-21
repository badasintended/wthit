package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.api.util.WNumbers;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * Component that renders an {@link ItemStack} with its name.
 */
@ApiSide.ClientOnly
public class NamedItemComponent implements ITooltipComponent {

    public static final NamedItemComponent EMPTY = new NamedItemComponent(ItemStack.EMPTY);

    public NamedItemComponent(ItemStack stack) {
        this.stack = stack;

        var count = stack.getCount();
        var name = stack.getHoverName().getString();
        this.label = count > 1 ? WNumbers.suffix(count) + " " + name : name;
    }

    public NamedItemComponent(ItemLike item) {
        this(new ItemStack(item));
    }

    public final ItemStack stack;
    public final String label;

    @Override
    public int getWidth() {
        return getFont().width(label) + 10;
    }

    @Override
    public int getHeight() {
        return getFont().lineHeight;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        var pose = ctx.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.scale(0.5f, 0.5f);
        ctx.renderItem(stack, 0, 0);
        pose.popMatrix();

        ctx.drawString(getFont(), label, x + 10, y, IApiService.INSTANCE.getFontColor());
    }

    private Font getFont() {
        return Minecraft.getInstance().font;
    }

}
