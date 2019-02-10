package mcp.mobius.waila.overlay;

import com.google.common.collect.Lists;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.RenderableTextComponent;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.text.TextComponent;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;

public class Tooltip {

    private final MinecraftClient client;
    private final List<Line> lines;
    private final boolean showItem;
    private final Dimension totalSize;

    public Tooltip(List<TextComponent> components, boolean showItem) {
        WailaTooltipEvent event = new WailaTooltipEvent(components, DataAccessor.INSTANCE);
        WailaTooltipEvent.WAILA_HANDLE_TOOLTIP.invoker().onTooltip(event);

        this.client = MinecraftClient.getInstance();
        this.lines = Lists.newArrayList();
        this.showItem = showItem;
        this.totalSize = new Dimension();

        computeLines(components);
        addPadding();
    }

    public void computeLines(List<TextComponent> components) {
        components.forEach(c -> {
            Dimension size = getLineSize(c);
            totalSize.setSize(Math.max(totalSize.width, size.width), totalSize.height + size.height);
            lines.add(new Line(c, size));
        });
    }

    public void addPadding() {
        totalSize.width += hasItem() ? 30 : 10;
        totalSize.height += 8;
    }

    public void draw() {
        Rectangle position = getPosition();
        WailaConfig.ConfigOverlay.ConfigOverlayColor color = Waila.CONFIG.get().getOverlay().getColor();

        position.x += hasItem() ? 26 : 6;
        position.width += hasItem() ? 24 : 4;
        position.y += 6;

        for (Line line : lines) {
            if (line.getComponent() instanceof RenderableTextComponent) {
                RenderableTextComponent component = (RenderableTextComponent) line.getComponent();
                int xOffset = 0;
                for (RenderableTextComponent.RenderContainer container : component.getRenderers()) {
                    Dimension size = container.getRenderer().getSize(container.getData(), DataAccessor.INSTANCE);
                    container.getRenderer().draw(container.getData(), DataAccessor.INSTANCE, position.x + xOffset, position.y);
                    xOffset += size.width;
                }
            } else {
                client.fontRenderer.drawWithShadow(line.getComponent().getFormattedText(), position.x, position.y, color.getFontColor());
            }
            position.y += line.size.height;
        }
    }

    private Dimension getLineSize(TextComponent component) {
        if (component instanceof RenderableTextComponent) {
            RenderableTextComponent renderable = (RenderableTextComponent) component;
            List<RenderableTextComponent.RenderContainer> renderers = renderable.getRenderers();
            if (renderers.isEmpty())
                return new Dimension(0, 0);

            int width = 0;
            int height = 0;
            for (RenderableTextComponent.RenderContainer container : renderers) {
                Dimension iconSize = container.getRenderer().getSize(container.getData(), DataAccessor.INSTANCE);
                width += iconSize.width;
                height = Math.max(height, iconSize.height);
            }

            return new Dimension(width, height);
        }

        return new Dimension(client.fontRenderer.getStringWidth(component.getFormattedText()), client.fontRenderer.fontHeight + 1);
    }

    public List<Line> getLines() {
        return lines;
    }

    public boolean hasItem() {
        return showItem && Waila.CONFIG.get().getGeneral().shouldShowItem() && !RayTracing.INSTANCE.getIdentifierStack().isEmpty();
    }

    public Rectangle getPosition() {
        Window window = MinecraftClient.getInstance().window;
        return new Rectangle(
                (int) (window.getScaledWidth() * Waila.CONFIG.get().getOverlay().getOverlayPosX() - totalSize.width / 2), // Center it
                (int) (window.getScaledHeight() * (1.0F - Waila.CONFIG.get().getOverlay().getOverlayPosY())),
                totalSize.width,
                totalSize.height
        );
    }

    public static class Line {

        private final TextComponent component;
        private final Dimension size;

        public Line(TextComponent component, Dimension size) {
            this.component = component;
            this.size = size;
        }

        public TextComponent getComponent() {
            return component;
        }

        public Dimension getSize() {
            return size;
        }
    }
}
