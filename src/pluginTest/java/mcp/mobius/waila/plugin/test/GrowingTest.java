package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public enum GrowingTest implements IBlockComponentProvider {

    INSTANCE;

    public static final ResourceLocation ENABLED = ResourceLocation.parse("test:grow");

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(ENABLED)) {
            var line = tooltip.getLine(WailaConstants.OBJECT_NAME_TAG);

            if (line != null) {
                line.with(new Component(0, 0xFF0000FF, 1))
                    .with(new Component(0, 0xFF00FF00, 2))
                    .with(new Component(0, 0xFF0000FF, 1))
                    .with(new Component(0, 0xFF0000FF, 1))
                    .with(new Component(10, 0xFFFF0000, 1));
            }
        }
    }

    private static class Component implements ITooltipComponent.HorizontalGrowing {

        private final int minimalWidth;
        private final int color;
        private final int weight;

        int width = 0;

        private Component(int minimalWidth, int color, int weight) {
            this.minimalWidth = minimalWidth;
            this.color = color;
            this.weight = weight;
        }

        @Override
        public int getMinimalWidth() {
            return minimalWidth;
        }

        @Override
        public void setGrownWidth(int grownWidth) {
            this.width = grownWidth;
        }

        @Override
        public int getHeight() {
            return 10;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
            ctx.fill(x, y, x + width, y + 10, color);
        }

    }

}
