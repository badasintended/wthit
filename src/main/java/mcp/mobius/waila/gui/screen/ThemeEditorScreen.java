package mcp.mobius.waila.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.Theme;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.gui.widget.ButtonEntry;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.InputValue;
import mcp.mobius.waila.gui.widget.value.IntInputValue;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

class ThemeEditorScreen extends ConfigScreen {

    private final WailaConfigScreen parent;
    private final Theme theme;
    private final boolean edit;
    private final TooltipRenderer previewRenderer;

    private InputValue<String> idVal;
    private InputValue<Integer> bgColorVal;
    private InputValue<Integer> gradStartVal;
    private InputValue<Integer> gradEndVal;
    private InputValue<Integer> textColorVal;

    public ThemeEditorScreen(WailaConfigScreen parent, Theme theme, boolean edit) {
        super(parent, CommonComponents.EMPTY, () -> {}, () -> {});

        this.parent = parent;
        this.theme = theme;
        this.edit = edit;
        this.previewRenderer = new PreviewTooltipRenderer();
    }

    @Override
    public void init() {
        super.init();
        parent.buildPreview(previewRenderer);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ConfigListWidget getOptions() {
        ConfigListWidget option = new ConfigListWidget(this, minecraft, width, height, 76, height - 32, 26, () -> {});

        idVal = new InputValue<>("config.waila.overlay_theme_editor.id",
            edit ? theme.getId().toString() : "", null, val -> {}, InputValue.IDENTIFIER);
        if (!edit) {
            option.add(idVal);
        }

        option
            .with(bgColorVal = new IntInputValue("config.waila.overlay_theme_editor.background_color",
                theme.getBackgroundColor(), null, val -> {}, IntFormat.RGB_HEX))
            .with(gradStartVal = new IntInputValue("config.waila.overlay_theme_editor.gradient_start",
                theme.getGradientStart(), null, val -> {}, IntFormat.RGB_HEX))
            .with(gradEndVal = new IntInputValue("config.waila.overlay_theme_editor.gradient_end",
                theme.getGradientEnd(), null, val -> {}, IntFormat.RGB_HEX))
            .with(textColorVal = new IntInputValue("config.waila.overlay_theme_editor.text_color",
                theme.getFontColor(), null, val -> {}, IntFormat.RGB_HEX));

        if (edit && !theme.getId().getNamespace().equals(WailaConstants.NAMESPACE)) {
            option.add(new ButtonEntry("config.waila.overlay_theme_editor.delete", 100, 20, button -> minecraft.setScreen(new ConfirmScreen(delete -> {
                if (delete) {
                    parent.removeTheme(theme.getId());
                    minecraft.setScreen(parent);
                } else {
                    minecraft.setScreen(this);
                }
            }, Component.translatable("config.waila.overlay_theme_editor.delete_prompt", theme.getId()), CommonComponents.EMPTY))));
        }

        return option;
    }

    @Override
    protected void renderForeground(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        previewRenderer.render(matrices, partialTicks);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onClose() {
        if (cancelled) {
            super.onClose();
            return;
        }

        if (idVal.getValue().isBlank()) {
            minecraft.getToasts().addToast(new SystemToast(
                SystemToast.SystemToastIds.TUTORIAL_HINT,
                Component.translatable("config.waila.missing_input"),
                Component.translatable("config.waila.overlay_theme_editor.id_empty")));
        } else {
            ResourceLocation id = new ResourceLocation(idVal.getValue());
            if (id.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) && !idVal.getValue().startsWith(ResourceLocation.DEFAULT_NAMESPACE + ":")) {
                id = new ResourceLocation("custom", id.getPath());
            }

            parent.addTheme(new Theme(id, bgColorVal.getValue(), gradStartVal.getValue(), gradEndVal.getValue(), textColorVal.getValue()));
            super.onClose();
        }
    }

    private class PreviewTooltipRenderer extends TooltipRenderer {

        protected PreviewTooltipRenderer() {
            super(false);
        }

        @Override
        protected float getScale() {
            return 2.0f;
        }

        @Override
        protected Align.X getXAnchor() {
            return Align.X.CENTER;
        }

        @Override
        protected Align.Y getYAnchor() {
            return Align.Y.TOP;
        }

        @Override
        protected Align.X getXAlign() {
            return Align.X.CENTER;
        }

        @Override
        protected Align.Y getYAlign() {
            return Align.Y.TOP;
        }

        @Override
        protected int getX() {
            return 0;
        }

        @Override
        protected int getY() {
            return 1;
        }

        @Override
        protected boolean bossBarsOverlap() {
            return false;
        }

        @Override
        protected int getBg() {
            return (0xFF << 24) + bgColorVal.getValue();
        }

        @Override
        protected int getGradStart() {
            return (0xFF << 24) + gradStartVal.getValue();
        }

        @Override
        protected int getGradEnd() {
            return (0xFF << 24) + gradEndVal.getValue();
        }

        @Override
        protected boolean enableTextToSpeech() {
            return false;
        }

        @Override
        public int getFontColor() {
            return textColorVal.getValue();
        }

    }

}
