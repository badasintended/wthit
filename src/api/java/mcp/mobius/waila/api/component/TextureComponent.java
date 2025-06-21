package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

/**
 * Component that renders a texture.
 */
@ApiSide.ClientOnly
public class TextureComponent implements ITooltipComponent {

    /**
     * <b>Note:</b> the texture must be 256x256 px, use other constructor if you have different sized texture.
     *
     * @param textureId the id of the texture
     * @param u         the left-most coordinate of the texture region
     * @param v         the top-most coordinate of the texture region
     * @param width     the width of the rectangle
     * @param height    the height of the rectangle
     */
    public TextureComponent(ResourceLocation textureId, int u, int v, int width, int height) {
        this(textureId, u, v, width, height, 256, 256);
    }

    /**
     * @param textureId     the id of the texture
     * @param u             the left-most coordinate of the texture region
     * @param v             the top-most coordinate of the texture region
     * @param width         the width of the rectangle
     * @param height        the height of the rectangle
     * @param textureWidth  the width of the entire texture
     * @param textureHeight the height of the entire texture
     */
    public TextureComponent(ResourceLocation textureId, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        this(textureId, width, height, u, v, width, height, textureWidth, textureHeight);
    }

    /**
     * @param textureId     the id of the texture
     * @param width         the width of the rectangle
     * @param height        the height of the rectangle
     * @param u             the left-most coordinate of the texture region
     * @param v             the top-most coordinate of the texture region
     * @param regionWidth   the width of the texture region
     * @param regionHeight  the height of the texture region
     * @param textureWidth  the width of the entire texture
     * @param textureHeight the height of the entire texture
     */
    public TextureComponent(ResourceLocation textureId, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    private final ResourceLocation textureId;
    private final int width, height;
    private final int u, v;
    private final int regionWidth, regionHeight;
    private final int textureWidth, textureHeight;

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        ctx.blit(RenderPipelines.GUI_TEXTURED, textureId, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

}
