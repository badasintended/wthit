package mcp.mobius.waila.plugin.harvest.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@ApiSide.ClientOnly
public class ToolComponent implements ITooltipComponent {

    private final ItemStack icon;
    private final @Nullable Boolean matches;

    public ToolComponent(ItemStack icon, @Nullable Boolean matches) {
        this.icon = icon;
        this.matches = matches;
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
        ctx.pose().scale(0.8f, 0.8f, 1f);
        ctx.pose().translate(x / 0.8f, y / 0.8f, 0);
        ctx.renderItem(icon, 0, 0);
        ctx.pose().popPose();

        if (matches == null) return;
        if (matches) {
            //TODO
        }
    }

}
