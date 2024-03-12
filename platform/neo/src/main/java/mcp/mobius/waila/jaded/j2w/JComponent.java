package mcp.mobius.waila.jaded.j2w;

import mcp.mobius.waila.api.ITooltipComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.ui.IElement;
import snownee.jade.overlay.OverlayRenderer;

public class JComponent implements ITooltipComponent {

    private final IElement element;
    private final Vec2 size;

    public JComponent(IElement element) {
        this.element = element;
        this.size = element.getSize();
    }

    @Override
    public int getWidth() {
        return (int) size.x;
    }

    @Override
    public int getHeight() {
        return (int) size.y;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, float delta) {
        OverlayRenderer.alpha = 1;
        element.render(ctx, x, y, x + getWidth(), y + getHeight());
    }

}
