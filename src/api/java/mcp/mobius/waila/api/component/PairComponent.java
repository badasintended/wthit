package mcp.mobius.waila.api.component;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * A tooltip component that renders key-value pair that would be aligned at the colon.
 */
@ApiSide.ClientOnly
public class PairComponent implements ITooltipComponent {

    public PairComponent(ITooltipComponent key, ITooltipComponent value) {
        this.key = key;
        this.value = value;
    }

    public PairComponent(Component key, Component value) {
        this(new WrappedComponent(key), new WrappedComponent(value));
    }

    public final ITooltipComponent key, value;

    @Override
    public int getWidth() {
        return key.getWidth() + getColonOffset() + getColonWidth() + value.getWidth();
    }

    @Override
    public int getHeight() {
        return Math.max(key.getHeight(), value.getHeight());
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        key.render(matrices, x, y, delta);
        Minecraft.getInstance().font.drawShadow(matrices, ": ", x + getColonOffset(), y, IWailaConfig.get().getOverlay().getColor().getFontColor());
        value.render(matrices, x + getColonOffset() + getColonWidth(), y, delta);
    }

    private int getColonOffset() {
        return IApiService.INSTANCE.getPairComponentColonOffset();
    }

    private int getColonWidth() {
        return IApiService.INSTANCE.getColonFontWidth();
    }

}
