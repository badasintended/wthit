package mcp.mobius.waila.api.__internal__;

import com.mojang.blaze3d.vertex.VertexConsumer;
import mcp.mobius.waila.api.ITooltipComponent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3x2f;

/** @hidden */
@ApiStatus.Internal
public interface IClientApiService {

    IClientApiService INSTANCE = Internals.loadService(IClientApiService.class);

    GuiRenderState getRenderState(GuiGraphics ctx);

    ScreenRectangle peekScissorStack(GuiGraphics ctx);

    void renderComponent(GuiGraphics ctx, ITooltipComponent component, int x, int y, DeltaTracker delta);

    void fillGradient(Matrix3x2f matrix, VertexConsumer buf, int x, int y, int w, int h, int start, int end);

    void renderRectBorder(Matrix3x2f matrix, VertexConsumer buf, int x, int y, int w, int h, int s, int gradStart, int gradEnd);

}
