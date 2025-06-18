package mcp.mobius.waila.plugin.harvest.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@ApiSide.ClientOnly
public class ToolComponent implements ITooltipComponent {

    private final @Nullable ItemStack icon;

    private final int v0;
    private final int width;
    private final int xo;

    private int x;

    public ToolComponent(@Nullable ItemStack icon, @Nullable Boolean matches) {
        this.icon = icon;

        if (matches != null) v0 = matches ? 7 : 0;
        else v0 = -1;

        if (icon == null) {
            width = 6;
            xo = 0;
        } else {
            width = 9;
            xo = 3;
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        this.x = x;
    }

    public void actuallyRender(GuiGraphics ctx, int y) {
        if (icon != null) {
            ctx.pose().pushMatrix();
            ctx.pose().translate(-2, -2);
            ctx.pose().scale(0.85f, 0.85f);
            ctx.pose().translate(x / 0.85f, y / 0.85f);
            ctx.renderItem(icon, 0, 0);
            ctx.pose().popMatrix();
        }

        if (v0 == -1) return;
        ctx.pose().pushMatrix();
        ctx.blit(RenderPipelines.GUI_TEXTURED, WailaConstants.COMPONENT_TEXTURE, x + xo, y + 3, 122, v0, 7, 7, 255, 255);
        ctx.pose().popMatrix();
    }

}
