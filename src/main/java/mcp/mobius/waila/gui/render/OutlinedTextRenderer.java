package mcp.mobius.waila.gui.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class OutlinedTextRenderer extends PictureInPictureRenderer<OutlinedTextRenderer.State> {

    public static State state(Component text, int x, int y, float scale, @Nullable ScreenRectangle scissorArea) {
        var font = Minecraft.getInstance().font;
        var x1 = x + font.width(text) + 2;
        var y1 = y + font.lineHeight + 2;
        return new State(text, x, y, x1, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x, y, x1, y1, scissorArea));
    }

    public OutlinedTextRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    protected void renderToTexture(State state, PoseStack matrices) {
        Minecraft.getInstance().font.drawInBatch8xOutline(state.text.getVisualOrderText(), state.x0 + 1, state.x1 + 1, 0xAAAAAA, 0x292929, matrices.last().pose(), bufferSource, 0xf000f0);
    }

    @Override
    public Class<State> getRenderStateClass() {
        return State.class;
    }

    @Override
    protected String getTextureLabel() {
        return WailaConstants.MOD_ID + ":bar";
    }

    public record State(
        Component text,
        int x0, int y0,
        int x1, int y1,
        float scale,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
    ) implements PictureInPictureRenderState {}

}
