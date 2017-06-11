package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.util.Point;

public class ItemStackDisplay extends WidgetBase {

    ItemStack stack = ItemStack.EMPTY;

    public ItemStackDisplay(IWidget parent) {
        this(parent, null);
    }

    public ItemStackDisplay(IWidget parent, ItemStack stack) {
        super(parent);
        this.stack = stack;
        this.setSize(16, 16);
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void draw(Point pos) {
        if (this.stack.isEmpty())
            return;

        float scaleX = this.getSize().getX() / 16.0F;
        float scaleY = this.getSize().getY() / 16.0F;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scaleX, scaleY, 1.0F);

        RenderHelper.enableGUIStandardItemLighting();
        DisplayUtil.renderStack((int) (pos.getX() / scaleX), (int) (pos.getY() / scaleX), this.stack);
        GlStateManager.popMatrix();
    }

}
