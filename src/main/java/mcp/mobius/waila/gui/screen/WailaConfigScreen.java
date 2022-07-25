package mcp.mobius.waila.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.config.Theme;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.gui.hud.Line;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.gui.widget.CategoryEntry;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.BooleanValue;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import mcp.mobius.waila.gui.widget.value.CycleValue;
import mcp.mobius.waila.gui.widget.value.EnumValue;
import mcp.mobius.waila.gui.widget.value.InputValue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.util.DisplayUtil.getAlphaFromPercentage;
import static mcp.mobius.waila.util.DisplayUtil.tryFormat;

public class WailaConfigScreen extends ConfigScreen {

    private static final Component PREVIEW_PROMPT = Component.translatable("config.waila.preview_prompt");

    private final WailaConfig defaultConfig = new WailaConfig();
    private final TooltipRenderer.State previewState = new PreviewTooltipRendererState();

    @Nullable
    private Theme theme;
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
    private ConfigValue<Integer> alphaVal;

    private ThemeValue themeIdVal;

    private KeyBindValue selectedKeyBind;

    public WailaConfigScreen(Screen parent) {
        super(parent, Component.translatable("gui.waila.configuration", WailaConstants.MOD_NAME), Waila.CONFIG::save, Waila.CONFIG::invalidate);
    }

    private static WailaConfig get() {
        return Waila.CONFIG.get();
    }

    public void buildPreview(TooltipRenderer.State state) {
        TooltipRenderer.beginBuild(state);
        TooltipRenderer.setIcon(new ItemComponent(Blocks.GRASS_BLOCK));
        TooltipRenderer.add(new Line(null).with(Component.literal(tryFormat(blockNameFormatVal.getValue(), Blocks.GRASS_BLOCK.getName().getString()))));
        TooltipRenderer.add(new Line(null).with(Component.literal("never gonna give you up").withStyle(ChatFormatting.OBFUSCATED)));
        TooltipRenderer.add(new Line(null).with(Component.literal(tryFormat(modNameFormatVal.getValue(), IModInfo.get(Blocks.GRASS_BLOCK).getName()))));
        TooltipRenderer.endBuild();
    }

    public void addTheme(Theme theme) {
        String id = theme.getId().toString();
        themeIdVal.addValue(id);
        themeIdVal.setValue(id);
        get().getOverlay().getColor().themes().put(theme.getId(), theme);
        this.theme = theme;
    }

    public void removeTheme(ResourceLocation id) {
        themeIdVal.removeValue(id.toString());
        get().getOverlay().getColor().themes().remove(id);
        this.theme = null;
    }

    private Theme getTheme() {
        if (theme == null) {
            theme = get().getOverlay().getColor().themes().get(new ResourceLocation(themeIdVal.getValue()));
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
        options.with(new CategoryEntry("config.waila.general"))
            .with(new BooleanValue("config.waila.display_tooltip",
                get().getGeneral().isDisplayTooltip(),
                defaultConfig.getGeneral().isDisplayTooltip(),
                val -> get().getGeneral().setDisplayTooltip(val)))
            .with(new BooleanValue("config.waila.sneaky_details",
                get().getGeneral().isShiftForDetails(),
                defaultConfig.getGeneral().isShiftForDetails(),
                val -> get().getGeneral().setShiftForDetails(val)))
            .with(new BooleanValue("config.waila.hide_sneak_text",
                get().getGeneral().isHideShiftText(),
                defaultConfig.getGeneral().isHideShiftText(),
                val -> get().getGeneral().setHideShiftText(val)))
            .with(new EnumValue<>("config.waila.display_mode",
                IWailaConfig.General.DisplayMode.values(),
                get().getGeneral().getDisplayMode(),
                defaultConfig.getGeneral().getDisplayMode(),
                val -> get().getGeneral().setDisplayMode(val)))
            .with(new BooleanValue("config.waila.hide_from_players",
                get().getGeneral().isHideFromPlayerList(),
                defaultConfig.getGeneral().isHideFromPlayerList(),
                val -> get().getGeneral().setHideFromPlayerList(val)))
            .with(new BooleanValue("config.waila.hide_from_debug",
                get().getGeneral().isHideFromDebug(),
                defaultConfig.getGeneral().isHideFromDebug(),
                val -> get().getGeneral().setHideFromDebug(val)))
            .with(new BooleanValue("config.waila.tts",
                get().getGeneral().isEnableTextToSpeech(),
                defaultConfig.getGeneral().isEnableTextToSpeech(),
                val -> get().getGeneral().setEnableTextToSpeech(val)))
            .with(new InputValue<>("config.waila.rate_limit",
                get().getGeneral().getRateLimit(),
                defaultConfig.getGeneral().getRateLimit(),
                val -> get().getGeneral().setRateLimit(Math.max(val, 250)),
                InputValue.POSITIVE_INTEGER))
            .with(new InputValue<>("config.waila.max_health_for_render",
                get().getGeneral().getMaxHealthForRender(),
                defaultConfig.getGeneral().getMaxHealthForRender(),
                val -> get().getGeneral().setMaxHealthForRender(val),
                InputValue.POSITIVE_INTEGER))
            .with(new InputValue<>("config.waila.max_hearts_per_line",
                get().getGeneral().getMaxHeartsPerLine(),
                defaultConfig.getGeneral().getMaxHeartsPerLine(),
                val -> get().getGeneral().setMaxHeartsPerLine(val),
                InputValue.POSITIVE_INTEGER));

        options.with(new CategoryEntry("config.waila.overlay"))
            .with(xAnchorValue = new EnumValue<>("config.waila.overlay_anchor_x",
                Align.X.values(),
                get().getOverlay().getPosition().getAnchor().getX(),
                defaultConfig.getOverlay().getPosition().getAnchor().getX(),
                val -> get().getOverlay().getPosition().getAnchor().setX(val)))
            .with(yAnchorValue = new EnumValue<>("config.waila.overlay_anchor_y",
                Align.Y.values(),
                get().getOverlay().getPosition().getAnchor().getY(),
                defaultConfig.getOverlay().getPosition().getAnchor().getY(),
                val -> get().getOverlay().getPosition().getAnchor().setY(val)))
            .with(xAlignValue = new EnumValue<>("config.waila.overlay_align_x",
                Align.X.values(),
                get().getOverlay().getPosition().getAlign().getX(),
                defaultConfig.getOverlay().getPosition().getAlign().getX(),
                val -> get().getOverlay().getPosition().getAlign().setX(val)))
            .with(yAlignValue = new EnumValue<>("config.waila.overlay_align_y",
                Align.Y.values(),
                get().getOverlay().getPosition().getAlign().getY(),
                defaultConfig.getOverlay().getPosition().getAlign().getY(),
                val -> get().getOverlay().getPosition().getAlign().setY(val)))
            .with(xPosValue = new InputValue<>("config.waila.overlay_pos_x",
                get().getOverlay().getPosition().getX(),
                defaultConfig.getOverlay().getPosition().getX(),
                val -> get().getOverlay().getPosition().setX(val),
                InputValue.INTEGER))
            .with(yPosValue = new InputValue<>("config.waila.overlay_pos_y",
                get().getOverlay().getPosition().getY(),
                defaultConfig.getOverlay().getPosition().getY(),
                val -> get().getOverlay().getPosition().setY(val),
                InputValue.INTEGER))
            .with(new BooleanValue("config.waila.boss_bars_overlap",
                get().getOverlay().getPosition().isBossBarsOverlap(),
                defaultConfig.getOverlay().getPosition().isBossBarsOverlap(),
                val -> get().getOverlay().getPosition().setBossBarsOverlap(val)))
            .with(scaleValue = new InputValue<>("config.waila.overlay_scale",
                get().getOverlay().getScale(),
                defaultConfig.getOverlay().getScale(),
                val -> get().getOverlay().setScale(Math.max(val, 0.0F)),
                InputValue.POSITIVE_DECIMAL))
            .with(alphaVal = new InputValue<>("config.waila.overlay_alpha",
                get().getOverlay().getColor().rawAlpha(),
                defaultConfig.getOverlay().getColor().rawAlpha(),
                val -> get().getOverlay().getColor().setAlpha(Math.min(100, Math.max(0, val))),
                InputValue.POSITIVE_INTEGER))
            .with(themeIdVal = new ThemeValue());

        options.with(new CategoryEntry("config.waila.formatting"))
            .with(modNameFormatVal = new InputValue<>("config.waila.format_mod_name",
                get().getFormatter().getModName(),
                defaultConfig.getFormatter().getModName(),
                val -> get().getFormatter().setModName(!val.contains("%s") ? get().getFormatter().getModName() : val),
                InputValue.ANY))
            .with(blockNameFormatVal = new InputValue<>("config.waila.format_block_name",
                get().getFormatter().getBlockName(),
                defaultConfig.getFormatter().getBlockName(),
                val -> get().getFormatter().setBlockName(!val.contains("%s") ? get().getFormatter().getBlockName() : val),
                InputValue.ANY))
            .with(new InputValue<>("config.waila.format_fluid_name",
                get().getFormatter().getFluidName(),
                defaultConfig.getFormatter().getFluidName(),
                val -> get().getFormatter().setFluidName(!val.contains("%s") ? get().getFormatter().getFluidName() : val),
                InputValue.ANY))
            .with(new InputValue<>("config.waila.format_entity_name",
                get().getFormatter().getEntityName(),
                defaultConfig.getFormatter().getEntityName(),
                val -> get().getFormatter().setEntityName(!val.contains("%s") ? get().getFormatter().getEntityName() : val),
                InputValue.ANY))
            .with(new InputValue<>("config.waila.format_registry_name",
                get().getFormatter().getRegistryName(),
                defaultConfig.getFormatter().getRegistryName(),
                val -> get().getFormatter().setRegistryName(!val.contains("%s") ? get().getFormatter().getRegistryName() : val),
                InputValue.ANY));

        options.with(new CategoryEntry("config.waila.keybinds"))
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
            super(key.getName(), key.key, key.getDefaultKey(), value -> {
                minecraft.options.setKey(key, value);
                KeyMapping.resetMapping();
            });

            this.button = new Button(0, 0, 100, 20, Component.empty(), w -> selectedKeyBind = this);
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

            button.x = x + width - button.getWidth();
            button.y = y + (height - button.getHeight()) / 2;
            button.render(matrices, mouseX, mouseY, partialTicks);
        }

    }

    private class ThemeValue extends CycleValue {

        private final Button editButton;
        private final Button newButton;

        public ThemeValue() {
            super("config.waila.overlay_theme",
                get().getOverlay().getColor().themes().values().stream().map(t -> t.getId().toString()).sorted(String::compareToIgnoreCase).toArray(String[]::new),
                get().getOverlay().getColor().theme().getId().toString(),
                val -> get().getOverlay().getColor().applyTheme(new ResourceLocation(val)),
                false);

            this.editButton = new Button(0, 0, 40, 20, Component.translatable("config.waila.edit"), button ->
                client.setScreen(new ThemeEditorScreen(WailaConfigScreen.this, getTheme(), true)));
            this.newButton = new Button(0, 0, 40, 20, Component.translatable("config.waila.new"), button ->
                client.setScreen(new ThemeEditorScreen(WailaConfigScreen.this, getTheme(), false)));

            editButton.active = !getValue().startsWith(WailaConstants.NAMESPACE + ":");
        }

        @Override
        public void addToScreen(ConfigScreen screen) {
            super.addToScreen(screen);
            screen.addListener(editButton);
            screen.addListener(newButton);
        }

        @Override
        public void setValue(String value) {
            super.setValue(value);
            editButton.active = !value.startsWith(WailaConstants.NAMESPACE + ":");
        }

        @Override
        protected void drawValue(PoseStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
            newButton.x = x + width - newButton.getWidth();
            newButton.y = y + (height - newButton.getHeight()) / 2;
            editButton.x = newButton.x - newButton.getWidth() - 2;
            editButton.y = newButton.y;
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

        private int getAlpha() {
            return getAlphaFromPercentage(alphaVal.getValue());
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
        public int getBg() {
            return getAlpha() + getTheme().getBackgroundColor();
        }

        @Override
        public int getGradStart() {
            return getAlpha() + getTheme().getGradientEnd();
        }

        @Override
        public int getGradEnd() {
            return getAlpha() + getTheme().getGradientEnd();
        }

        @Override
        public boolean enableTextToSpeech() {
            return false;
        }

        @Override
        public int getFontColor() {
            return getTheme().getFontColor();
        }

    }


}
