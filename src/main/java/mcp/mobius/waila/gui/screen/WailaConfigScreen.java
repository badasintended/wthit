package mcp.mobius.waila.gui.screen;

import java.awt.Rectangle;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.gui.hud.Line;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.gui.hud.theme.ThemeDefinition;
import mcp.mobius.waila.gui.widget.ButtonEntry;
import mcp.mobius.waila.gui.widget.CategoryEntry;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.BooleanValue;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import mcp.mobius.waila.gui.widget.value.CycleValue;
import mcp.mobius.waila.gui.widget.value.EnumValue;
import mcp.mobius.waila.gui.widget.value.InputValue;
import mcp.mobius.waila.mixin.KeyMappingAccess;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.util.DisplayUtil.createButton;
import static mcp.mobius.waila.util.DisplayUtil.tryFormat;

public class WailaConfigScreen extends ConfigScreen {

    private static final Component PREVIEW_PROMPT = Component.translatable(Tl.Config.PREVIEW_PROMPT);

    private final WailaConfig defaultConfig = new WailaConfig();
    private final TooltipRenderer.State previewState = new PreviewTooltipRendererState();

    @Nullable
    private ThemeDefinition<?> theme;
    private boolean f1held = false;

    private ConfigValue<String> modNameFormatVal;
    private ConfigValue<String> blockNameFormatVal;
    private ConfigValue<Integer> xPosValue;
    private ConfigValue<Align.X> xAnchorValue;
    private ConfigValue<Align.Y> yAnchorValue;
    private ConfigValue<Align.X> xAlignValue;
    private ConfigValue<Align.Y> yAlignValue;
    private ConfigValue<Integer> yPosValue;
    private ConfigValue<Float> scaleValue;
    private ConfigValue<Integer> backgroundAlphaVal;

    private ThemeValue themeIdVal;

    private KeyBindValue selectedKeyBind;

    public WailaConfigScreen(Screen parent) {
        super(parent, Component.translatable(Tl.Gui.CONFIGURATION, WailaConstants.MOD_NAME), Waila.CONFIG::save, Waila.CONFIG::invalidate);
    }

    private static WailaConfig get() {
        return Waila.CONFIG.get();
    }

    public Rectangle buildPreview(TooltipRenderer.State state) {
        TooltipRenderer.beginBuild(state);
        TooltipRenderer.setIcon(new ItemComponent(Blocks.GRASS_BLOCK));
        TooltipRenderer.add(new Line(null).with(Component.literal(tryFormat(blockNameFormatVal.getValue(), Blocks.GRASS_BLOCK.getName().getString()))));
        TooltipRenderer.add(new Line(null).with(Component.literal("never gonna give you up").withStyle(ChatFormatting.OBFUSCATED)));
        TooltipRenderer.add(new Line(null).with(Component.literal(tryFormat(modNameFormatVal.getValue(), IModInfo.get(Blocks.GRASS_BLOCK).getName()))));
        return TooltipRenderer.endBuild();
    }

    public void addTheme(ThemeDefinition<?> theme) {
        get().getOverlay().getColor().getCustomThemes().put(theme.id, theme);
        ThemeDefinition.resetAll();

        String id = theme.id.toString();
        themeIdVal.addValue(id);
        themeIdVal.setValue(id);

        this.theme = theme;
    }

    public void removeTheme(ResourceLocation id) {
        get().getOverlay().getColor().getCustomThemes().remove(id);
        ThemeDefinition.resetAll();

        themeIdVal.removeValue(id.toString());
        this.theme = null;
    }

    private ThemeDefinition<?> getTheme() {
        if (theme == null) {
            theme = ThemeDefinition.getAll().get(new ResourceLocation(themeIdVal.getValue()));
        }
        return theme;
    }

    @Override
    public void render(@NotNull PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        if (InputConstants.isKeyDown(minecraft.getWindow().getWindow(), InputConstants.KEY_F1)) {
            if (!f1held) {
                f1held = true;
                buildPreview(previewState);
            }

            renderBackground(matrices);
            TooltipRenderer.render(matrices, partialTicks);
        } else {
            TooltipRenderer.resetState();
            f1held = false;
            theme = null;
            super.render(matrices, mouseX, mouseY, partialTicks);
            drawCenteredString(matrices, font, PREVIEW_PROMPT, width / 2, 22, 0xAAAAAA);
        }
    }

    @Override
    public ConfigListWidget getOptions() {
        ConfigListWidget options = new ConfigListWidget(this, minecraft, width, height, 42, height - 32, 26, Waila.CONFIG::save);
        options.with(new CategoryEntry(Tl.Config.GENERAL))
            .with(new BooleanValue(Tl.Config.DISPLAY_TOOLTIP,
                get().getGeneral().isDisplayTooltip(),
                defaultConfig.getGeneral().isDisplayTooltip(),
                val -> get().getGeneral().setDisplayTooltip(val)))
            .with(new BooleanValue(Tl.Config.SNEAKY_DETAILS,
                get().getGeneral().isShiftForDetails(),
                defaultConfig.getGeneral().isShiftForDetails(),
                val -> get().getGeneral().setShiftForDetails(val)))
            .with(new BooleanValue(Tl.Config.HIDE_SNEAK_TEXT,
                get().getGeneral().isHideShiftText(),
                defaultConfig.getGeneral().isHideShiftText(),
                val -> get().getGeneral().setHideShiftText(val)))
            .with(new EnumValue<>(Tl.Config.DISPLAY_MODE,
                IWailaConfig.General.DisplayMode.values(),
                get().getGeneral().getDisplayMode(),
                defaultConfig.getGeneral().getDisplayMode(),
                val -> get().getGeneral().setDisplayMode(val)))
            .with(new BooleanValue(Tl.Config.HIDE_FROM_PLAYERS,
                get().getGeneral().isHideFromPlayerList(),
                defaultConfig.getGeneral().isHideFromPlayerList(),
                val -> get().getGeneral().setHideFromPlayerList(val)))
            .with(new BooleanValue(Tl.Config.HIDE_FROM_DEBUG,
                get().getGeneral().isHideFromDebug(),
                defaultConfig.getGeneral().isHideFromDebug(),
                val -> get().getGeneral().setHideFromDebug(val)))
            .with(new BooleanValue(Tl.Config.TTS,
                get().getGeneral().isEnableTextToSpeech(),
                defaultConfig.getGeneral().isEnableTextToSpeech(),
                val -> get().getGeneral().setEnableTextToSpeech(val)))
            .with(new InputValue<>(Tl.Config.RATE_LIMIT,
                get().getGeneral().getRateLimit(),
                defaultConfig.getGeneral().getRateLimit(),
                val -> get().getGeneral().setRateLimit(Math.max(val, 250)),
                InputValue.POSITIVE_INTEGER))
            .with(new ButtonEntry(Tl.Config.BLACKLIST, Tl.Config.BLACKLIST_OPEN, 100, 20, w ->
                Util.getPlatform().openFile(Waila.BLACKLIST_CONFIG.getPath().toFile())));

        options.with(new CategoryEntry(Tl.Config.OVERLAY))
            .with(xAnchorValue = new EnumValue<>(Tl.Config.OVERLAY_ANCHOR_X,
                Align.X.values(),
                get().getOverlay().getPosition().getAnchor().getX(),
                defaultConfig.getOverlay().getPosition().getAnchor().getX(),
                val -> get().getOverlay().getPosition().getAnchor().setX(val)))
            .with(yAnchorValue = new EnumValue<>(Tl.Config.OVERLAY_ANCHOR_Y,
                Align.Y.values(),
                get().getOverlay().getPosition().getAnchor().getY(),
                defaultConfig.getOverlay().getPosition().getAnchor().getY(),
                val -> get().getOverlay().getPosition().getAnchor().setY(val)))
            .with(xAlignValue = new EnumValue<>(Tl.Config.OVERLAY_ALIGN_X,
                Align.X.values(),
                get().getOverlay().getPosition().getAlign().getX(),
                defaultConfig.getOverlay().getPosition().getAlign().getX(),
                val -> get().getOverlay().getPosition().getAlign().setX(val)))
            .with(yAlignValue = new EnumValue<>(Tl.Config.OVERLAY_ALIGN_Y,
                Align.Y.values(),
                get().getOverlay().getPosition().getAlign().getY(),
                defaultConfig.getOverlay().getPosition().getAlign().getY(),
                val -> get().getOverlay().getPosition().getAlign().setY(val)))
            .with(xPosValue = new InputValue<>(Tl.Config.OVERLAY_POS_X,
                get().getOverlay().getPosition().getX(),
                defaultConfig.getOverlay().getPosition().getX(),
                val -> get().getOverlay().getPosition().setX(val),
                InputValue.INTEGER))
            .with(yPosValue = new InputValue<>(Tl.Config.OVERLAY_POS_Y,
                get().getOverlay().getPosition().getY(),
                defaultConfig.getOverlay().getPosition().getY(),
                val -> get().getOverlay().getPosition().setY(val),
                InputValue.INTEGER))
            .with(new BooleanValue(Tl.Config.BOSS_BARS_OVERLAP,
                get().getOverlay().getPosition().isBossBarsOverlap(),
                defaultConfig.getOverlay().getPosition().isBossBarsOverlap(),
                val -> get().getOverlay().getPosition().setBossBarsOverlap(val)))
            .with(scaleValue = new InputValue<>(Tl.Config.OVERLAY_SCALE,
                get().getOverlay().getScale(),
                defaultConfig.getOverlay().getScale(),
                val -> get().getOverlay().setScale(Math.max(val, 0.0F)),
                InputValue.POSITIVE_DECIMAL))
            .with(backgroundAlphaVal = new InputValue<>(Tl.Config.OVERLAY_BACKGROUND_ALPHA,
                get().getOverlay().getColor().getBackgroundAlpha(),
                defaultConfig.getOverlay().getColor().getBackgroundAlpha(),
                val -> get().getOverlay().getColor().setBackgroundAlpha(Mth.clamp(val, 0x00, 0xFF)),
                InputValue.POSITIVE_INTEGER))
            .with(themeIdVal = new ThemeValue());

        options.with(new CategoryEntry(Tl.Config.FORMATTING))
            .with(modNameFormatVal = new InputValue<>(Tl.Config.FORMAT_MOD_NAME,
                get().getFormatter().getModName(),
                defaultConfig.getFormatter().getModName(),
                val -> get().getFormatter().setModName(!val.contains("%s") ? get().getFormatter().getModName() : val),
                InputValue.ANY))
            .with(blockNameFormatVal = new InputValue<>(Tl.Config.FORMAT_BLOCK_NAME,
                get().getFormatter().getBlockName(),
                defaultConfig.getFormatter().getBlockName(),
                val -> get().getFormatter().setBlockName(!val.contains("%s") ? get().getFormatter().getBlockName() : val),
                InputValue.ANY))
            .with(new InputValue<>(Tl.Config.FORMAT_FLUID_NAME,
                get().getFormatter().getFluidName(),
                defaultConfig.getFormatter().getFluidName(),
                val -> get().getFormatter().setFluidName(!val.contains("%s") ? get().getFormatter().getFluidName() : val),
                InputValue.ANY))
            .with(new InputValue<>(Tl.Config.FORMAT_ENTITY_NAME,
                get().getFormatter().getEntityName(),
                defaultConfig.getFormatter().getEntityName(),
                val -> get().getFormatter().setEntityName(!val.contains("%s") ? get().getFormatter().getEntityName() : val),
                InputValue.ANY))
            .with(new InputValue<>(Tl.Config.FORMAT_REGISTRY_NAME,
                get().getFormatter().getRegistryName(),
                defaultConfig.getFormatter().getRegistryName(),
                val -> get().getFormatter().setRegistryName(!val.contains("%s") ? get().getFormatter().getRegistryName() : val),
                InputValue.ANY));

        options.with(new CategoryEntry(Tl.Config.KEYBINDS))
            .with(new KeyBindValue(WailaClient.keyOpenConfig))
            .with(new KeyBindValue(WailaClient.keyShowOverlay))
            .with(new KeyBindValue(WailaClient.keyToggleLiquid))
            .with(new KeyBindValue(WailaClient.keyShowRecipeInput))
            .with(new KeyBindValue(WailaClient.keyShowRecipeOutput));

        return options;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedKeyBind != null) {
            selectedKeyBind.setValue(InputConstants.Type.MOUSE.getOrCreate(button));
            selectedKeyBind = null;
            return true;
        }

        return f1held || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selectedKeyBind != null) {
            if (keyCode == InputConstants.KEY_ESCAPE) {
                selectedKeyBind.setValue(InputConstants.UNKNOWN);
            } else {
                selectedKeyBind.setValue(InputConstants.getKey(keyCode, scanCode));
            }

            selectedKeyBind = null;
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public class KeyBindValue extends ConfigValue<InputConstants.Key> {

        private final Button button;

        public KeyBindValue(KeyMapping key) {
            super(key.getName(), ((KeyMappingAccess) key).wthit_key(), key.getDefaultKey(), value -> {
                minecraft.options.setKey(key, value);
                KeyMapping.resetMapping();
            });

            this.button = createButton(0, 0, 100, 20, Component.empty(), w -> selectedKeyBind = this);
        }

        @Override
        public GuiEventListener getListener() {
            return button;
        }

        @Override
        protected void drawValue(PoseStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
            if (selectedKeyBind == this) {
                button.setMessage(Component.literal("> " + getValue().getDisplayName().getString() + " <").withStyle(ChatFormatting.YELLOW));
            } else {
                button.setMessage(getValue().getDisplayName());
            }

            button.setX(x + width - button.getWidth());
            button.setY(y + (height - button.getHeight()) / 2);
            button.render(matrices, mouseX, mouseY, partialTicks);
        }

    }

    private class ThemeValue extends CycleValue {

        private final Button editButton;
        private final Button newButton;

        public ThemeValue() {
            super(Tl.Config.OVERLAY_THEME,
                ThemeDefinition.getAll().values().stream().map(t -> t.id.toString()).sorted(String::compareToIgnoreCase).toArray(String[]::new),
                get().getOverlay().getColor().getActiveTheme().toString(),
                val -> get().getOverlay().getColor().applyTheme(new ResourceLocation(val)),
                false);

            this.editButton = createButton(0, 0, 40, 20, Component.translatable(Tl.Config.EDIT), button ->
                client.setScreen(new ThemeEditorScreen(WailaConfigScreen.this, getTheme(), true)));
            this.newButton = createButton(0, 0, 40, 20, Component.translatable(Tl.Config.NEW), button ->
                client.setScreen(new ThemeEditorScreen(WailaConfigScreen.this, getTheme(), false)));

            reloadEditButton();
        }

        private void reloadEditButton() {
            editButton.active = !ThemeDefinition.getAll().get(new ResourceLocation(getValue())).builtin;
        }

        @Override
        protected void gatherChildren(ImmutableList.Builder<GuiEventListener> children) {
            super.gatherChildren(children);
            children.add(editButton);
            children.add(newButton);
        }

        @Override
        public void setValue(String value) {
            super.setValue(value);
            reloadEditButton();
        }

        @Override
        protected void drawValue(PoseStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
            newButton.setX(x + width - newButton.getWidth());
            newButton.setY(y + (height - newButton.getHeight()) / 2);
            editButton.setX(newButton.getX() - newButton.getWidth() - 2);
            editButton.setY(newButton.getY());
            editButton.render(matrices, mouseX, mouseY, partialTicks);
            newButton.render(matrices, mouseX, mouseY, partialTicks);

            super.drawValue(matrices, width - 84, height, x, y, mouseX, mouseY, selected, partialTicks);
        }

    }

    private class PreviewTooltipRendererState implements TooltipRenderer.State {

        @Override
        public boolean render() {
            return true;
        }

        @Override
        public boolean fireEvent() {
            return false;
        }

        @Override
        public int getBackgroundAlpha() {
            return backgroundAlphaVal.getValue();
        }

        @Override
        public float getScale() {
            return scaleValue.getValue();
        }

        @Override
        public Align.X getXAnchor() {
            return xAnchorValue.getValue();
        }

        @Override
        public Align.Y getYAnchor() {
            return yAnchorValue.getValue();
        }

        @Override
        public Align.X getXAlign() {
            return xAlignValue.getValue();
        }

        @Override
        public Align.Y getYAlign() {
            return yAlignValue.getValue();
        }

        @Override
        public int getX() {
            return xPosValue.getValue();
        }

        @Override
        public int getY() {
            return yPosValue.getValue();
        }

        @Override
        public boolean bossBarsOverlap() {
            return false;
        }

        @Override
        public ITheme getTheme() {
            return WailaConfigScreen.this.getTheme().getInitializedInstance();
        }

        @Override
        public boolean enableTextToSpeech() {
            return false;
        }

    }


}
