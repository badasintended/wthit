package mcp.mobius.waila.service;

import com.mojang.blaze3d.vertex.VertexConsumer;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import mcp.mobius.waila.gui.hud.ComponentRenderer;
import mcp.mobius.waila.util.DisplayUtil;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;

public class ClientApiService implements IClientApiService {

    @Override
    public void renderComponent(GuiGraphics ctx, ITooltipComponent component, int x, int y, float delta) {
        ComponentRenderer.get().render(ctx, component, x, y, component.getWidth(), component.getHeight(), delta);
    }

    @Override
    public void fillGradient(Matrix4f matrix, VertexConsumer buf, int x, int y, int w, int h, int start, int end) {
        DisplayUtil.fillGradient(matrix, buf, x, y, w, h, start, end);
    }

    @Override
    public void renderRectBorder(Matrix4f matrix, VertexConsumer buf, int x, int y, int w, int h, int s, int gradStart, int gradEnd) {
        DisplayUtil.renderRectBorder(matrix, buf, x, y, w, h, s, gradStart, gradEnd);
    }

    @Override
    public IWailaConfig getConfig() {
        return WailaClient.CONFIG.get();
    }

}
