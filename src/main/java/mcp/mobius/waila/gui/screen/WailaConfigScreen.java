package mcp.mobius.waila.gui.screen;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import org.joml.Vector2i;

import static mcp.mobius.waila.util.DisplayUtil.createButton;
import static mcp.mobius.waila.util.DisplayUtil.tryFormat;

public class WailaConfigScreen extends TabbedConfigScreen {

    public static final Component TITLE = Component.translatable(Tl.Gui.WAILA_SETTINGS, WailaConstants.MOD_NAME);
    private static final Component PREVIEW_PROMPT = Component.translatable(Tl.Config.PREVIEW_PROMPT);

    private final WailaConfig defaultConfig = new WailaConfig();
    private final TooltipRenderer.State previewState = new PreviewTooltipRendererState();

    @Nullable
    private ThemeDefinition<?> theme;
    private boolean f1held = false;

    private InputValue<String> modNameFormatVal;
    private InputValue<String> blockNameFormatVal;
    private InputValue<Integer> fpsVal;

    private ButtonEntry placementButton;
    private EnumValue<Align.X> xAnchorValue;
    private EnumValue<Align.Y> yAnchorValue;
    private EnumValue<Align.X> xAlignValue;
    private EnumValue<Align.Y> yAlignValue;
    private InputValue<Integer> xPosValue;
    private InputValue<Integer> yPosValue;
    private InputValue<Float> scaleValue;

    private InputValue<Integer> backgroundAlphaVal;

    private ThemeValue themeIdVal;

    private @Nullable KeyBindValue selectedKeyBind;

    public WailaConfigScreen(Screen parent) {
        super(parent, CommonComponents.EMPTY, Waila.CONFIG::save, Waila.CONFIG::invalidate);
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

        var id = theme.id.toString();
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
            theme = ThemeDefinition.getAll().get(ResourceLocation.parse(themeIdVal.getValue()));
        }
        return theme;
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        if (InputConstants.isKeyDown(minecraft.getWindow(), InputConstants.KEY_F1)) {
            if (!f1held) {
                f1held = true;
                buildPreview(previewState);
            }

            TooltipRenderer.render(ctx, minecraft.getDeltaTracker());
        } else {
            TooltipRenderer.resetState();
            f1held = false;
            theme = null;
            super.render(ctx, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public ConfigListWidget getOptions() {
        var options = new ConfigListWidget(this, minecraft, width, height, 24, height - 32, 26, Waila.CONFIG::save);
        options.headerSeparator = false;

        options.with(new CategoryEntry(Tl.Config.GENERAL)
            .with(new BooleanValue(Tl.Config.VANILLA_OPTIONS,
                get().getGeneral().vanillaOptions(),
                defaultConfig.getGeneral().vanillaOptions(),
                val -> get().getGeneral().setVanillaOptions(val)))
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
                Util.getPlatform().openFile(Waila.BLACKLIST_CONFIG.getPath().toFile()))));

        options.with(new CategoryEntry(Tl.Config.OVERLAY)
            .with(fpsVal = Util.make(new InputValue<>(Tl.Config.OVERLAY_FPS,
                    get().getOverlay().getFps(),
                    defaultConfig.getOverlay().getFps(),
                    val -> get().getOverlay().setFps(val),
                    InputValue.POSITIVE_INTEGER),
                it -> it.disable(Tl.Config.OverlayFps.DISABLED_REASON)))
            .with(placementButton = new ButtonEntry(Tl.Config.OVERLAY_PLACEMENT, 100, 20, w ->
                minecraft.setScreen(new PlacementScreen())))
            .withHidden(xAnchorValue = new EnumValue<>(Tl.Config.OVERLAY_ANCHOR,
                Align.X.values(),
                get().getOverlay().getPosition().getAnchor().getX(),
                defaultConfig.getOverlay().getPosition().getAnchor().getX(),
                val -> get().getOverlay().getPosition().getAnchor().setX(val)))
            .withHidden(yAnchorValue = new EnumValue<>(Tl.Config.OVERLAY_ANCHOR,
                Align.Y.values(),
                get().getOverlay().getPosition().getAnchor().getY(),
                defaultConfig.getOverlay().getPosition().getAnchor().getY(),
                val -> get().getOverlay().getPosition().getAnchor().setY(val)))
            .withHidden(xAlignValue = new EnumValue<>(Tl.Config.OVERLAY_ANCHOR,
                Align.X.values(),
                get().getOverlay().getPosition().getAlign().getX(),
                defaultConfig.getOverlay().getPosition().getAlign().getX(),
                val -> get().getOverlay().getPosition().getAlign().setX(val)))
            .withHidden(yAlignValue = new EnumValue<>(Tl.Config.OVERLAY_ANCHOR,
                Align.Y.values(),
                get().getOverlay().getPosition().getAlign().getY(),
                defaultConfig.getOverlay().getPosition().getAlign().getY(),
                val -> get().getOverlay().getPosition().getAlign().setY(val)))
            .withHidden(xPosValue = new InputValue<>(Tl.Config.OVERLAY_OFFSET,
                get().getOverlay().getPosition().getX(),
                defaultConfig.getOverlay().getPosition().getX(),
                val -> get().getOverlay().getPosition().setX(val),
                InputValue.INTEGER))
            .withHidden(yPosValue = new InputValue<>(Tl.Config.OVERLAY_OFFSET,
                get().getOverlay().getPosition().getY(),
                defaultConfig.getOverlay().getPosition().getY(),
                val -> get().getOverlay().getPosition().setY(val),
                InputValue.INTEGER))
            .with(new BooleanValue(Tl.Config.BOSS_BARS_OVERLAP,
                get().getOverlay().getPosition().isBossBarsOverlap(),
                defaultConfig.getOverlay().getPosition().isBossBarsOverlap(),
                val -> get().getOverlay().getPosition().setBossBarsOverlap(val)))
            .withHidden(scaleValue = new InputValue<>(Tl.Config.OVERLAY_SCALE,
                get().getOverlay().getScale(),
                defaultConfig.getOverlay().getScale(),
                val -> get().getOverlay().setScale(Math.max(val, 0.0F)),
                InputValue.POSITIVE_DECIMAL))
            .with(backgroundAlphaVal = new InputValue<>(Tl.Config.OVERLAY_BACKGROUND_ALPHA,
                get().getOverlay().getColor().getBackgroundAlpha(),
                defaultConfig.getOverlay().getColor().getBackgroundAlpha(),
                val -> get().getOverlay().getColor().setBackgroundAlpha(Mth.clamp(val, 0x00, 0xFF)),
                InputValue.POSITIVE_INTEGER))
            .with(themeIdVal = new ThemeValue()));

        options.with(new CategoryEntry(Tl.Config.FORMATTING)
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
                InputValue.ANY)));

        options.with(new CategoryEntry(Tl.Config.KEYBINDS)
            .with(new KeyBindValue(WailaClient.keyOpenConfig))
            .with(new KeyBindValue(WailaClient.keyShowOverlay))
            .with(new KeyBindValue(WailaClient.keyToggleLiquid))
            .with(new KeyBindValue(WailaClient.keyShowRecipeInput))
            .with(new KeyBindValue(WailaClient.keyShowRecipeOutput)));

        return options;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        if (selectedKeyBind != null) {
            selectedKeyBind.setValue(InputConstants.Type.MOUSE.getOrCreate(event.button()));
            selectedKeyBind = null;
            return true;
        }

        return f1held || super.mouseClicked(event, doubled);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (selectedKeyBind != null) {
            if (event.key() == InputConstants.KEY_ESCAPE) {
                selectedKeyBind.setValue(InputConstants.UNKNOWN);
            } else {
                selectedKeyBind.setValue(InputConstants.getKey(event));
            }

            selectedKeyBind = null;
            return true;
        }

        return super.keyPressed(event);
    }

    public class KeyBindValue extends ConfigValue<InputConstants.Key, KeyBindValue> {

        private final Button button;

        public KeyBindValue(KeyMapping key) {
            super(key.getName(), ((KeyMappingAccess) key).wthit_key(), key.getDefaultKey(), value -> {
                key.setKey(value);
                KeyMapping.resetMapping();
            });

            this.button = createButton(0, 0, 100, 20, Component.empty(), w -> selectedKeyBind = this);
        }

        @Override
        public GuiEventListener getListener() {
            return button;
        }

        @Override
        protected void drawValue(GuiGraphics ctx, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
            if (selectedKeyBind == this) {
                button.setMessage(Component.literal("> " + getValue().getDisplayName().getString() + " <").withStyle(ChatFormatting.YELLOW));
            } else {
                button.setMessage(getValue().getDisplayName());
            }

            button.setX(x + width - button.getWidth());
            button.setY(y + (height - button.getHeight()) / 2);
            button.render(ctx, mouseX, mouseY, partialTicks);
        }

    }

    private class ThemeValue extends CycleValue {

        private final Button editButton;
        private final Button newButton;

        public ThemeValue() {
            super(Tl.Config.OVERLAY_THEME,
                ThemeDefinition.getAll().values().stream().map(t -> t.id.toString()).sorted(String::compareToIgnoreCase).toArray(String[]::new),
                get().getOverlay().getColor().getActiveTheme().toString(),
                val -> get().getOverlay().getColor().applyTheme(ResourceLocation.parse(val)),
                false);

            this.editButton = createButton(0, 0, 40, 20, Component.translatable(Tl.Config.EDIT), button ->
                client.setScreen(new ThemeEditorScreen(WailaConfigScreen.this, getTheme(), true)));
            this.newButton = createButton(0, 0, 40, 20, Component.translatable(Tl.Config.NEW), button ->
                client.setScreen(new ThemeEditorScreen(WailaConfigScreen.this, getTheme(), false)));

            reloadEditButton();
        }

        private void reloadEditButton() {
            editButton.active = !ThemeDefinition.getAll().get(ResourceLocation.parse(getValue())).builtin;
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
        protected void drawValue(GuiGraphics ctx, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
            newButton.setX(x + width - newButton.getWidth());
            newButton.setY(y + (height - newButton.getHeight()) / 2);
            editButton.setX(newButton.getX() - newButton.getWidth() - 2);
            editButton.setY(newButton.getY());
            editButton.render(ctx, mouseX, mouseY, partialTicks);
            newButton.render(ctx, mouseX, mouseY, partialTicks);

            super.drawValue(ctx, width - 84, height, x, y, mouseX, mouseY, selected, partialTicks);
        }

    }

    private class PlacementScreen extends YesIAmSureTheClientInstanceIsPresentByTheTimeIUseItScreen {

        static final Object WATCHER_OFFSET = new Object();
        static final Object WATCHER_UPDATE = new Object();

        final MutableObject<Component> textAlign = new MutableObject<>(Component.translatable(Tl.Config.OVERLAY_ALIGN));
        final MutableObject<Component> textAnchor = new MutableObject<>(Component.translatable(Tl.Config.OVERLAY_ANCHOR));
        final MutableObject<Component> textOffset = new MutableObject<>(Component.translatable(Tl.Config.OVERLAY_OFFSET));
        final MutableObject<Component> textScale = new MutableObject<>(Component.translatable(Tl.Config.OVERLAY_SCALE));

        final Rectangle rect = new Rectangle();
        float oldScale;

        boolean drag;
        Vector2i dragPos = new Vector2i();
        Vector2d dragStart = new Vector2d();
        Vector2d dragNow = new Vector2d();

        Button xAlign, yAlign, xAnchor, yAnchor;
        InputValue<?>.WatchedTextfield xPos, yPos, scale;
        Button done;

        int x, y;
        int maxTextWidth;

        public PlacementScreen() {
            super(Component.translatable(Tl.Config.OVERLAY));
        }

        private void resetOffset(ConfigValue<?, ?> value) {
            if (!value.isChanged()) return;
            xPosValue.setValue(0);
            yPosValue.setValue(0);
        }

        private void addUpdateWatcher(MutableObject<Component> text, ConfigValue<?, ?>... cvs) {
            for (var cv : cvs) {
                cv.addWatcher(WATCHER_UPDATE, v -> {
                    if (!Arrays.stream(cvs).allMatch(ConfigValue::isValueValid)) {
                        text.setValue(text.getValue().plainCopy().withStyle(ChatFormatting.ITALIC, ChatFormatting.RED));
                    } else if (Arrays.stream(cvs).anyMatch(ConfigValue::isChanged)) {
                        text.setValue(text.getValue().plainCopy().withStyle(ChatFormatting.ITALIC, ChatFormatting.YELLOW));
                    } else {
                        text.setValue(text.getValue().plainCopy());
                    }
                });
            }
        }

        @Override
        protected void init() {
            super.init();

            addRenderableWidget(xAnchor = xAnchorValue.getListener());
            addRenderableWidget(yAnchor = yAnchorValue.getListener());
            addUpdateWatcher(textAnchor, xAnchorValue, yAnchorValue);
            xAnchorValue.addWatcher(WATCHER_OFFSET, this::resetOffset);
            yAnchorValue.addWatcher(WATCHER_OFFSET, this::resetOffset);
            xAnchor.setWidth(50);
            yAnchor.setWidth(50);

            addRenderableWidget(xAlign = xAlignValue.getListener());
            addRenderableWidget(yAlign = yAlignValue.getListener());
            addUpdateWatcher(textAlign, xAlignValue, yAlignValue);
            xAlign.setWidth(50);
            yAlign.setWidth(50);

            addRenderableWidget(xPos = xPosValue.getListener());
            addRenderableWidget(yPos = yPosValue.getListener());
            addUpdateWatcher(textOffset, xPosValue, yPosValue);
            xPos.grow = yPos.grow = false;
            xPos.setWidth(50);
            yPos.setWidth(50);

            addRenderableWidget(scale = scaleValue.getListener());
            addUpdateWatcher(textScale, scaleValue);
            oldScale = scaleValue.getValue();
            scale.grow = false;
            scale.setWidth(102);
            scaleValue.addWatcher(WATCHER_OFFSET, v -> {
                if (v.isChanged()) resetOffset(v);
                oldScale = v.getValue();
            });

            addRenderableWidget(done = createButton(0, 0, 102, 20, CommonComponents.GUI_DONE, (b) -> {
                var list = List.of(xAnchorValue, yAnchorValue, xAlignValue, yAlignValue, xPosValue, yPosValue, scaleValue);
                if (!list.stream().allMatch(ConfigValue::isValueValid)) {
                    ConfigListWidget.showErrorToast(minecraft);
                    return;
                }

                var message = Component.translatable(Tl.Config.OVERLAY_PLACEMENT);
                var changed = list.stream().anyMatch(ConfigValue::isChanged);
                if (changed) message.withStyle(ChatFormatting.ITALIC, ChatFormatting.YELLOW);
                placementButton.setTitle(message);

                minecraft.setScreen(WailaConfigScreen.this);
            }));

            tick();
        }

        @Override
        @SuppressWarnings({"ConstantValue", "UnusedAssignment"})
        public void tick() {
            maxTextWidth = minecraft.font.width(textAlign.getValue());
            maxTextWidth = Math.max(maxTextWidth, minecraft.font.width(textAnchor.getValue()));
            maxTextWidth = Math.max(maxTextWidth, minecraft.font.width(textOffset.getValue()));
            maxTextWidth = Math.max(maxTextWidth, minecraft.font.width(textScale.getValue()));

            var r = buildPreview(previewState);
            var s = (float) scaleValue.getValue();
            rect.setBounds((int) (r.x * s), (int) (r.y * s), (int) (r.width * s), (int) (r.height * s));

            var optWidth = maxTextWidth + 10 + 50 + 2 + 50;
            var optHeight = 22 * 5;

            x = width - optWidth - 10;
            y = height - optHeight - 10;

            if (rect.intersects(x, y, optWidth, optHeight)) {
                x = 10;
                y = 10;
            }

            var i = 0;
            var optX = x + maxTextWidth + 10;

            xAnchor.setPosition(optX, y + (22 * i++));
            yAnchor.setPosition(xAnchor.getX() + xAnchor.getWidth() + 2, xAnchor.getY());

            xAlign.setPosition(optX, y + (22 * i++));
            yAlign.setPosition(xAlign.getX() + xAlign.getWidth() + 2, xAlign.getY());

            xPos.setPosition(optX, y + (22 * i++));
            yPos.setPosition(xPos.getX() + xPos.getWidth() + 2, xPos.getY());

            scale.setPosition(optX, y + (22 * i++));
            done.setPosition(optX, y + (22 * i++));
        }

        @SuppressWarnings({"ConstantValue", "UnusedAssignment"})
        @Override
        public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
            super.render(ctx, mouseX, mouseY, delta);

            if (rect.contains(mouseX, (double) mouseY)) {
                ctx.requestCursor(CursorTypes.RESIZE_ALL);
            }

            var i = 0;
            var y = this.y + minecraft.font.lineHeight / 2;
            // @formatter:off
            ctx.drawString(minecraft.font, textAnchor.getValue(), x, y + (22 * i++), 0xFFFFFFFF);
            ctx.drawString(minecraft.font, textAlign .getValue(), x, y + (22 * i++), 0xFFFFFFFF);
            ctx.drawString(minecraft.font, textOffset.getValue(), x, y + (22 * i++), 0xFFFFFFFF);
            ctx.drawString(minecraft.font, textScale .getValue(), x, y + (22 * i++), 0xFFFFFFFF);
            // @formatter:on

            TooltipRenderer.render(ctx, minecraft.getDeltaTracker());
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
            if (rect.contains(event.x(), event.y())) {
                drag = true;
                dragPos.set(xPosValue.getValue(), yPosValue.getValue());
                dragStart.x = dragNow.x = event.x();
                dragStart.y = dragNow.y = event.y();
            }

            return super.mouseClicked(event, doubled);
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double offsetX, double offsetY) {
            if (drag) {
                var scale = (float) scaleValue.getValue();
                xPosValue.setValue((int) (dragPos.x + (dragNow.x - dragStart.x) / scale));
                yPosValue.setValue((int) (dragPos.y + (dragNow.y - dragStart.y) / scale));
                dragNow.x = event.x();
                dragNow.y = event.y();
                return true;
            }

            return super.mouseDragged(event, offsetX, offsetY);
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            drag = false;
            return super.mouseReleased(event);
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            var offset = event.hasControlDown() ? 10 : 1;

            switch (event.key()) {
                case InputConstants.KEY_UP -> yPosValue.setValue(yPosValue.getValue() - offset);
                case InputConstants.KEY_DOWN -> yPosValue.setValue(yPosValue.getValue() + offset);
                case InputConstants.KEY_LEFT -> xPosValue.setValue(xPosValue.getValue() - offset);
                case InputConstants.KEY_RIGHT -> xPosValue.setValue(xPosValue.getValue() + offset);
                default -> {
                    return super.keyPressed(event);
                }
            }

            return true;
        }

        @Override
        public boolean shouldCloseOnEsc() {
            return false;
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
        public int getFps() {
            return fpsVal.getValue();
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
