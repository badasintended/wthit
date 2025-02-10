package mcp.mobius.waila.service;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import mcp.mobius.waila.util.DisplayUtil;
import net.minecraft.world.item.ItemStack;

public class ClientApiService implements IClientApiService {

    @Override
    public void renderComponent(PoseStack matrices, ITooltipComponent component, int x, int y, float delta) {
        DisplayUtil.renderComponent(matrices, component, x, y, 0, delta);
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
    public void renderItem(int x, int y, ItemStack stack) {
        DisplayUtil.renderStack(x, y, stack);
    }

}
