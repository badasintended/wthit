package mcp.mobius.waila.api.component;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/**
 * A tooltip component that renders key-value pair that would be aligned at the colon.
 */
@ApiSide.ClientOnly
public class PairComponent implements ITooltipComponent {

    public PairComponent(Component key, Component value) {
        this(new WrappedComponent(key), new WrappedComponent(value));
    }

    public PairComponent(ITooltipComponent key, ITooltipComponent value) {
        this.key = key;
        this.value = value;

        height = Math.max(key.getHeight(), value.getHeight());
    }

    public final ITooltipComponent key, value;
    private final int height;

    @Override
    public int getWidth() {
        return getColonOffset() + getColonWidth() + value.getWidth();
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        int offset = key.getHeight() < height ? (height - key.getHeight()) / 2 : 0;
        IApiService.INSTANCE.renderComponent(matrices, key, x, y + offset, delta);

        Font font = Minecraft.getInstance().font;
        offset = font.lineHeight < height ? (height - font.lineHeight) / 2 : 0;
        font.drawShadow(matrices, ": ", x + getColonOffset(), y + offset, IWailaConfig.get().getOverlay().getColor().getTheme().getDefaultTextColor());

        offset = value.getHeight() < height ? (height - value.getHeight()) / 2 : 0;
        IApiService.INSTANCE.renderComponent(matrices, value, x + getColonOffset() + getColonWidth(), y + offset, delta);
    }

    private int getColonOffset() {
        return IApiService.INSTANCE.getPairComponentColonOffset();
    }

    private int getColonWidth() {
        return IApiService.INSTANCE.getColonFontWidth();
    }

}
