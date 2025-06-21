package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Component that renders key-value pair that would be aligned at the colon.
 */
@ApiSide.ClientOnly
public class PairComponent implements ITooltipComponent {

    public PairComponent(Component key, Component value) {
        this(new WrappedComponent(key), new WrappedComponent(value));
    }

    public PairComponent(ITooltipComponent key, ITooltipComponent value) {
        this.key = key;
        this.value = value;
    }

    public final ITooltipComponent key, value;

    private int width = -1;
    private int height = -1;

    @Override
    public int getWidth() {
        if (width == -1) {
            key.getWidth(); // if there is special computation
            width = getColonOffset() + getColonWidth() + value.getWidth();
        }

        return width;
    }

    @Override
    public int getHeight() {
        if (height == -1) height = Math.max(key.getHeight(), value.getHeight());
        return height;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        var offset = key.getHeight() < height ? (height - key.getHeight()) / 2 : 0;
        IClientApiService.INSTANCE.renderComponent(ctx, key, x, y + offset, delta);

        var font = Minecraft.getInstance().font;
        offset = font.lineHeight < height ? (height - font.lineHeight) / 2 : 0;
        ctx.drawString(font, ": ", x + getColonOffset(), y + offset, IApiService.INSTANCE.getFontColor());

        offset = value.getHeight() < height ? (height - value.getHeight()) / 2 : 0;
        IClientApiService.INSTANCE.renderComponent(ctx, value, x + getColonOffset() + getColonWidth(), y + offset, delta);
    }

    private int getColonOffset() {
        return IApiService.INSTANCE.getPairComponentColonOffset();
    }

    private int getColonWidth() {
        return IApiService.INSTANCE.getColonFontWidth();
    }

}
