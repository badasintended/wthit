package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaHelper;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
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

    private static final Font FONT = Minecraft.getInstance().font;
    public static final int HEIGHT = FONT.lineHeight;

    public NamedItemComponent(ItemStack stack) {
        this.stack = stack;
    }

    public NamedItemComponent(ItemLike item) {
        this(new ItemStack(item));
    }

    public final ItemStack stack;

    private String getText() {
        var count = stack.getCount();
        var name = stack.getHoverName().getString();
        return count > 1 ? WailaHelper.suffix(count) + " " + name : name;
    }

    @Override
    public int getWidth() {
        return FONT.width(getText()) + 10;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, float delta) {
        var pose = ctx.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(0.5f, 0.5f, 0.5f);
        ctx.renderItem(stack, 0, 0);
        pose.popPose();

        ctx.drawString(FONT, getText(), x + 10, y, IApiService.INSTANCE.getFontColor());
    }

}
