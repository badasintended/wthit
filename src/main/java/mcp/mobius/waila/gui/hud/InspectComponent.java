package mcp.mobius.waila.gui.hud;

import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.ITooltipComponent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("deprecation")
public class InspectComponent implements ITooltipComponent {

    public final ITooltipComponent actual;
    public final IPluginInfo plugin;
    public final Class<?> provider;
    public final ResourceLocation tag;

    public InspectComponent(ITooltipComponent actual, IPluginInfo plugin, Class<?> provider, ResourceLocation tag) {
        this.actual = actual;
        this.plugin = plugin;
        this.provider = provider;
        this.tag = tag;
    }

    @Override
    public int getWidth() {
        return actual.getWidth();
    }

    @Override
    public int getHeight() {
        return actual.getHeight();
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        actual.render(ctx, x, y, delta);
    }

    public static class Growing extends InspectComponent implements HorizontalGrowing {

        public final HorizontalGrowing actual;

        public Growing(HorizontalGrowing actual, IPluginInfo plugin, Class<?> provider, ResourceLocation tag) {
            super(actual, plugin, provider, tag);
            this.actual = actual;
        }

        @Override
        public int getMinimalWidth() {
            return actual.getMinimalWidth();
        }

        @Override
        public void setGrownWidth(int grownWidth) {
            actual.setGrownWidth(grownWidth);
        }

    }

}
