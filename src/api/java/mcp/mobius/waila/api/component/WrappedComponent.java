package mcp.mobius.waila.api.component;

import java.util.Objects;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/**
 * A tooltip component that renders a vanilla {@link Component}.
 */
@ApiSide.ClientOnly
public class WrappedComponent implements ITooltipComponent {

    public WrappedComponent(Component component) {
        this.component = component;
    }

    public final Component component;

    @Override
    public int getWidth() {
        return getFont().width(component);
    }

    @Override
    public int getHeight() {
        return getFont().lineHeight;
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        int color = IWailaConfig.get().getOverlay().getColor().getFontColor();
        getFont().drawShadow(matrices, component, x, y, color);
    }

    private Font getFont() {
        return Minecraft.getInstance().font;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WrappedComponent that = (WrappedComponent) o;
        return component.equals(that.component);
    }

    @Override
    public int hashCode() {
        return Objects.hash(component);
    }

}
