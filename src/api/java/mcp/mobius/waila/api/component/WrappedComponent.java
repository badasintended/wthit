package mcp.mobius.waila.api.component;

import java.util.List;
import java.util.Objects;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

/**
 * Component that renders a vanilla {@link Component}.
 */
@ApiSide.ClientOnly
public class WrappedComponent implements ITooltipComponent {

    public WrappedComponent(String literal) {
        this(Component.literal(literal));
    }

    public WrappedComponent(Component component) {
        this.component = component;
    }

    public final Component component;
    private List<FormattedCharSequence> lines;
    private int height;

    @Override
    public int getWidth() {
        var font = getFont();
        var split = font.getSplitter().splitLines(component, Integer.MAX_VALUE, Style.EMPTY);
        lines = Language.getInstance().getVisualOrder(split);

        var width = lines.stream().mapToInt(font::width).max().orElse(0);
        height = font.lineHeight * split.size();

        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        var font = getFont();

        for (var line : lines) {
            ctx.drawString(font, line, x, y, IApiService.INSTANCE.getFontColor());
            y += font.lineHeight;
        }
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
        var that = (WrappedComponent) o;
        return component.equals(that.component);
    }

    @Override
    public int hashCode() {
        return Objects.hash(component);
    }

}
