package mcp.mobius.waila.gui.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.gui.hud.ComponentRenderer;
import mcp.mobius.waila.gui.hud.InspectComponent;
import mcp.mobius.waila.gui.hud.TooltipHandler;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.util.Log;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class InspectorScreen extends YesIAmSureTheClientInstanceIsPresentByTheTimeIUseItScreen {

    private static final Log LOG = Log.create();
    private static final String API_COMPONENTS = "mcp.mobius.waila.api.component.";
    private static final Component TITLE = Component.translatable(Tl.Gui.Inspector.TITLE);
    private static final State STATE = new State();

    private final Renderer renderer = new Renderer();

    private boolean tickSuccess = false;

    private final List<ITooltipComponent> hoveredComponent = new ArrayList<>();

    public InspectorScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        super.init();

        InspectComponent.wrap = true;
        tickSuccess = TooltipHandler.tick(STATE, true);
        InspectComponent.wrap = false;
    }

    @Override
    public void onClose() {
        super.onClose();
        TooltipHandler.tick();
    }

    @Override
    public void render(@NotNull PoseStack matrices, int mouseX, int mouseY, float tickDelta) {
        super.render(matrices, mouseX, mouseY, tickDelta);

        if (!hoveredComponent.isEmpty()) {
            var h = minecraft.font.lineHeight + 2;

            var component = hoveredComponent.get(0);
            if (component instanceof InspectComponent wrapper) component = wrapper.actual;
            var clazz = component.getClass().getName();
            if (clazz.startsWith(API_COMPONENTS)) clazz = clazz.substring(API_COMPONENTS.length());
            drawString(matrices, minecraft.font, Component.literal(clazz), 5, 5, 0xFFFFFF);

            var y = 1;
            var wrapper = (InspectComponent) hoveredComponent.get(hoveredComponent.size() - 1);
            var tag = wrapper.tag == null ? null : wrapper.tag.toString();
            if (tag != null) drawString(matrices, minecraft.font, Component.translatable(Tl.Gui.Inspector.TAG, tag), 5, 5 + h * (y++), 0xFFFFFF);

            var provider = wrapper.origin.instance().getClass().getName();
            drawString(matrices, minecraft.font, Component.translatable(Tl.Gui.Inspector.PROVIDER, provider), 5, 5 + h * (y++), 0xFFFFFF);

            var pluginId = wrapper.origin.plugin().getPluginId().toString();
            drawString(matrices, minecraft.font, Component.translatable(Tl.Gui.Inspector.PLUGIN_ID, pluginId), 5, 5 + h * (y++), 0xFFFFFF);

            var mod = wrapper.origin.plugin().getModInfo();
            drawString(matrices, minecraft.font, Component.translatable(Tl.Gui.Inspector.MOD, mod.getName(), mod.getId()), 5, 5 + h * y, 0xFFFFFF);
        }

        if (tickSuccess) {
            renderer.mouseX = mouseX;
            renderer.mouseY = mouseY;
            hoveredComponent.clear();

            ComponentRenderer.set(renderer);
            TooltipRenderer.render(matrices, tickDelta);
            ComponentRenderer.set(null);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!hoveredComponent.isEmpty()) {
            var sb = new StringBuilder();

            var wrapper = (InspectComponent) hoveredComponent.get(hoveredComponent.size() - 1);
            var tag = wrapper.tag == null ? null : wrapper.tag.toString();
            if (tag != null) sb.append("tag: ").append(tag).append("\n\t");

            var provider = wrapper.origin.instance().getClass().getName();
            sb.append("provider: ").append(provider).append("\n\t");

            var pluginId = wrapper.origin.plugin().getPluginId().toString();
            sb.append("pluginId: ").append(pluginId).append("\n\t");

            var mod = wrapper.origin.plugin().getModInfo();
            sb.append("modName: ").append(mod.getName()).append("\n\t");
            sb.append("modId: ").append(mod.getId());

            LOG.info(sb.toString());
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private class Renderer extends ComponentRenderer {

        int mouseX, mouseY;

        @Override
        public void render(PoseStack matrices, ITooltipComponent component, int x, int y, int cw, int ch, float delta) {
            component.render(matrices, x, y, delta);

            var v = 0.3f;
            if (x < mouseX && mouseX < (x + cw) && y < mouseY && mouseY < (y + ch)) {
                hoveredComponent.add(component);
                v = 1f;
            }

            ComponentRenderer.Default.renderBounds(matrices, x, y, cw, ch, v);
        }

    }

    private static class State implements TooltipRenderer.State {

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }

        @Override
        public boolean render() {
            return true;
        }

        @Override
        public boolean fireEvent() {
            return true;
        }

        @Override
        public int getFps() {
            return 0;
        }

        @Override
        public float getScale() {
            return 1;
        }

        @Override
        public IWailaConfig.Overlay.Position.Align.X getXAnchor() {
            return IWailaConfig.Overlay.Position.Align.X.CENTER;
        }

        @Override
        public IWailaConfig.Overlay.Position.Align.Y getYAnchor() {
            return IWailaConfig.Overlay.Position.Align.Y.MIDDLE;
        }

        @Override
        public IWailaConfig.Overlay.Position.Align.X getXAlign() {
            return IWailaConfig.Overlay.Position.Align.X.CENTER;
        }

        @Override
        public IWailaConfig.Overlay.Position.Align.Y getYAlign() {
            return IWailaConfig.Overlay.Position.Align.Y.MIDDLE;
        }

        @Override
        public boolean bossBarsOverlap() {
            return false;
        }

        @Override
        public boolean enableTextToSpeech() {
            return false;
        }

        @Override
        public int getBackgroundAlpha() {
            return 0xFF;
        }

        @Override
        public ITheme getTheme() {
            return WailaClient.CONFIG.get().getOverlay().getColor().getTheme();
        }

    }

}
