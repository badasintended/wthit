package mcp.mobius.waila.gui.hud;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.registry.PluginAware;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class InspectComponent implements ITooltipComponent {

    public static boolean wrap = false;

    public final ITooltipComponent actual;
    public final PluginAware<?> origin;
    public final @Nullable ResourceLocation tag;

    private InspectComponent(ITooltipComponent actual, PluginAware<?> origin, @Nullable ResourceLocation tag) {
        this.actual = actual;
        this.origin = origin;
        this.tag = tag;
    }

    public static @Nullable ITooltipComponent maybeWrap(@Nullable ITooltipComponent actual, @Nullable PluginAware<?> origin, @Nullable ResourceLocation tag) {
        if (!wrap || actual == null || origin == null || actual instanceof InspectComponent) {
            return actual;
        }

        if (actual instanceof ITooltipComponent.HorizontalGrowing hg) {
            return new InspectComponent.Growing(hg, origin, tag);
        }

        return new InspectComponent(actual, origin, tag);
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

        public Growing(HorizontalGrowing actual, PluginAware<?> origin, ResourceLocation tag) {
            super(actual, origin, tag);
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
