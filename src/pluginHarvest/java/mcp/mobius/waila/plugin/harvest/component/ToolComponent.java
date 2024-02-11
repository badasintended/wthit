package mcp.mobius.waila.plugin.harvest.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@ApiSide.ClientOnly
public class ToolComponent implements ITooltipComponent {

    private final ItemStack icon;
    private final int v0;

    public ToolComponent(ItemStack icon, @Nullable Boolean matches) {
        this.icon = icon;

        if (matches != null) v0 = matches ? 7 : 0;
        else v0 = -1;
    }

    @Override
    public int getWidth() {
        return 10;
    }

    @Override
    public int getHeight() {
        return Minecraft.getInstance().font.lineHeight;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, float delta) {
        ctx.pose().pushPose();
        ctx.pose().translate(-1, -2, 0);
        ctx.pose().scale(0.85f, 0.85f, 1f);
        ctx.pose().translate(x / 0.85f, y / 0.85f, 0);
        ctx.renderItem(icon, 0, 0);
        ctx.pose().popPose();

        if (v0 == -1) return;
        ctx.pose().pushPose();
        ctx.pose().translate(0, 0, ItemRenderer.ITEM_COUNT_BLIT_OFFSET);
        ctx.blit(WailaConstants.COMPONENT_TEXTURE, x + 4, y + 3, 122, v0, 7, 7);
        ctx.pose().popPose();
    }

}
