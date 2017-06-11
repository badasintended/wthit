package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.IWidget;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Point;

public class PictureDisplay extends WidgetBase {

    protected ResourceLocation texture;

    public PictureDisplay(IWidget parent, String uri) {
        super(parent);
        this.texture = new ResourceLocation(uri);
    }

    @Override
    public void draw(Point pos) {
        this.saveGLState();

        GlStateManager.pushMatrix();
        this.texManager.bindTexture(texture);
        UIHelper.drawTexture(pos.getX(), pos.getY(), this.getSize().getX(), this.getSize().getY());
        GlStateManager.popMatrix();

        this.loadGLState();
    }
}
