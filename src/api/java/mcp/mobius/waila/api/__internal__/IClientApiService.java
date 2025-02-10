package mcp.mobius.waila.api.__internal__;

import com.mojang.blaze3d.vertex.VertexConsumer;
import mcp.mobius.waila.api.ITooltipComponent;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

/** @hidden */
@ApiStatus.Internal
public interface IClientApiService {

    IClientApiService INSTANCE = Internals.loadService(IClientApiService.class);

    void renderComponent(GuiGraphics ctx, ITooltipComponent component, int x, int y, float delta);

    void fillGradient(Matrix4f matrix, VertexConsumer buf, int x, int y, int w, int h, int start, int end);

    void renderRectBorder(Matrix4f matrix, VertexConsumer buf, int x, int y, int w, int h, int s, int gradStart, int gradEnd);

}
