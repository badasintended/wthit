package mcp.mobius.waila.gui.hud;

import java.util.Random;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.util.WRenders;
import mcp.mobius.waila.util.DisplayUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public interface ComponentRenderer {

    Random RANDOM = new Random();

    void render(GuiGraphics ctx, ITooltipComponent component, int x, int y, int cw, int ch, DeltaTracker delta);

    ComponentRenderer DEFAULT = (ctx, component, x, y, cw, ch, delta) -> {
        component.render(ctx, x, y, delta);

        if (WailaClient.showComponentBounds) {
            ctx.pose().pushPose();
            var scale = (float) Minecraft.getInstance().getWindow().getGuiScale();
            ctx.pose().scale(1 / scale, 1 / scale, 1);

            var buf = WRenders.buffer(RenderType.gui());
            var bx = Mth.floor(x * scale + 0.5);
            var by = Mth.floor(y * scale + 0.5);
            var bw = Mth.floor(cw * scale + 0.5);
            var bh = Mth.floor(ch * scale + 0.5);
            var color = (0xFF << 24) + Mth.hsvToRgb(RANDOM.nextFloat(), RANDOM.nextFloat(), 1f);
            DisplayUtil.renderRectBorder(ctx.pose().last().pose(), buf, bx, by, bw, bh, 1, color, color);

            ctx.pose().popPose();
            ctx.flush();
        }
    };

}
