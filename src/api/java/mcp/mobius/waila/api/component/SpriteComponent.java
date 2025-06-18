package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

@ApiSide.ClientOnly
public class SpriteComponent implements ITooltipComponent {

    public SpriteComponent(ResourceLocation id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    private final ResourceLocation id;
    private final int width, height;

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
        ctx.blitSprite(RenderPipelines.GUI_TEXTURED, id, x, y, width, height);
    }

}
