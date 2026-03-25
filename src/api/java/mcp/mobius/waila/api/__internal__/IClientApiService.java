package mcp.mobius.waila.api.__internal__;

import com.mojang.blaze3d.vertex.VertexConsumer;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

/** @hidden */
@ApiStatus.Internal
public interface IClientApiService {

    IClientApiService INSTANCE = Internals.loadService(IClientApiService.class);

    GuiRenderState getRenderState(GuiGraphicsExtractor ctx);

    @Nullable ScreenRectangle peekScissorStack(GuiGraphicsExtractor ctx);

    void renderComponent(GuiGraphicsExtractor ctx, ITooltipComponent component, int x, int y, DeltaTracker delta);

    void fillGradient(Matrix3x2f matrix, VertexConsumer buf, int x, int y, int w, int h, int start, int end);

    void renderRectBorder(Matrix3x2f matrix, VertexConsumer buf, int x, int y, int w, int h, int s, int gradStart, int gradEnd);

    IWailaConfig getConfig();

}
