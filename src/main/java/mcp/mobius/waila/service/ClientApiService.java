package mcp.mobius.waila.service;

import com.mojang.blaze3d.vertex.VertexConsumer;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import mcp.mobius.waila.util.DisplayUtil;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;

public class ClientApiService implements IClientApiService {

    @Override
    public void renderComponent(GuiGraphics ctx, ITooltipComponent component, int x, int y, float delta) {
        DisplayUtil.renderComponent(ctx, component, x, y, 0, delta);
    }

    @Override
    public void fillGradient(Matrix4f matrix, VertexConsumer buf, int x, int y, int w, int h, int start, int end) {
        DisplayUtil.fillGradient(matrix, buf, x, y, w, h, start, end);
    }

    @Override
    public void renderRectBorder(Matrix4f matrix, VertexConsumer buf, int x, int y, int w, int h, int s, int gradStart, int gradEnd) {
        DisplayUtil.renderRectBorder(matrix, buf, x, y, w, h, s, gradStart, gradEnd);
    }

}
