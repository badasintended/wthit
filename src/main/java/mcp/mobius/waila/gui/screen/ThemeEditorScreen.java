package mcp.mobius.waila.gui.screen;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.gui.hud.theme.ThemeAccessor;
import mcp.mobius.waila.gui.hud.theme.ThemeDefinition;
import mcp.mobius.waila.gui.hud.theme.ThemeType;
import mcp.mobius.waila.gui.widget.ButtonEntry;
import mcp.mobius.waila.gui.widget.CategoryEntry;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.BooleanValue;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import mcp.mobius.waila.gui.widget.value.CycleValue;
import mcp.mobius.waila.gui.widget.value.EnumValue;
import mcp.mobius.waila.gui.widget.value.InputValue;
import mcp.mobius.waila.gui.widget.value.IntInputValue;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.TypeUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

class ThemeEditorScreen extends ConfigScreen {

    private final WailaConfigScreen parent;
    private final ThemeDefinition<?> template;
    private final boolean edit;

    private final TooltipRenderer.State previewState;

    private ThemeType<?> type;
    private ITheme theme;

    private ConfigListWidget options;
    private InputValue<String> idVal;
    private CycleValue typeVal;

    private final Map<ThemeType<?>, Map<String, Object>> type2attr = new HashMap<>();

    public ThemeEditorScreen(WailaConfigScreen parent, ThemeDefinition<?> template, boolean edit) {
        super(parent, CommonComponents.EMPTY, () -> {}, () -> {});

        this.parent = parent;
        this.template = template;
        this.edit = edit;
        this.previewState = new PreviewTooltipRendererState();

        this.type = template.type;
        type2attr.put(type, new HashMap<>(type.properties.size()));
        type.properties.forEach((key, prop) -> type2attr.get(type).put(key, prop.get(template.instance)));

        buildTheme();
    }

    private void buildTheme() {
        this.theme = type.create(type2attr.get(type));
        theme.processProperties(ThemeAccessor.INSTANCE);
    }

    @Override
    public void init() {
        super.init();
        parent.buildPreview(previewState);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ConfigListWidget getOptions() {
        options = new ConfigListWidget(this, minecraft, width, height, parent.buildPreview(previewState).height * 2 + 4, height - 32, 26, () -> {});

        if (idVal == null) {
            idVal = new InputValue<>(Tl.Config.OverlayThemeEditor.ID,
                edit ? template.id.toString() : "", null, val -> {}, InputValue.IDENTIFIER);
        }

        if (edit) {
            idVal.disable(null);
        }

        if (typeVal == null) {
            typeVal = new CycleValue(Tl.Config.OverlayThemeEditor.TYPE,
                Registrar.INSTANCE.themeTypes.keySet().stream().map(ResourceLocation::toString).sorted(String::compareToIgnoreCase).toArray(String[]::new),
                type.getId().toString(), val -> {}, false) {

                @Override
                public void setValue(String value) {
                    super.setValue(value);

                    type = Registrar.INSTANCE.themeTypes.get(new ResourceLocation(value));
                    options.save();
                    rebuildOptions();
                }

            };
        }

        Map<String, ConfigValue<Object>> attrValues = new HashMap<>(type.properties.size());

        options
            .with(new ButtonEntry(Tl.Config.OverlayThemeEditor.REFRESH, 100, 20, button -> {
                options.save();
                buildTheme();
                options.resize(parent.buildPreview(previewState).height * 2 + 4, height - 32);
                type.properties.forEach((key, prop) -> attrValues.get(key).setValue(prop.get(theme)));
            }))
            .with(idVal)
            .with(typeVal)
            .with(new CategoryEntry(Tl.Config.OverlayThemeEditor.ATTRIBUTES));

        type2attr.computeIfAbsent(type, t -> new HashMap<>(t.properties.size()));

        type.properties.forEach((key, prop) -> {
            Class<?> propType = prop.type;
            Map<String, Object> attr = type2attr.get(type);
            Object templateValue = attr.computeIfAbsent(key, k -> prop.defaultValue);
            ConfigValue<?> value;

            if (propType == int.class) {
                value = new IntInputValue(prop.getTlKey(), TypeUtil.uncheckedCast(templateValue), null, val -> attr.put(key, val), TypeUtil.uncheckedCast(prop.context));
            } else if (propType == boolean.class) {
                value = new BooleanValue(prop.getTlKey(), TypeUtil.uncheckedCast(templateValue), null, val -> attr.put(key, val));
            } else if (propType == double.class) {
                value = new InputValue<>(prop.getTlKey(), TypeUtil.uncheckedCast(templateValue), null, val -> attr.put(key, val), InputValue.DECIMAL);
            } else if (propType == String.class) {
                value = new InputValue<>(prop.getTlKey(), TypeUtil.uncheckedCast(templateValue), null, val -> attr.put(key, val), InputValue.ANY);
            } else if (propType.isEnum()) {
                value = new EnumValue<>(prop.getTlKey(), TypeUtil.uncheckedCast(propType.getEnumConstants()), TypeUtil.uncheckedCast(templateValue), null, val -> attr.put(key, val));
            } else {
                throw new IllegalArgumentException("Invalid property type " + propType.getSimpleName());
            }

            value.setId(key);
            attrValues.put(key, TypeUtil.uncheckedCast(value));
            options.add(value);
        });

        if (edit) {
            options
                .with(new CategoryEntry(Tl.Config.OverlayThemeEditor.DELETE))
                .with(new ButtonEntry(Tl.Config.OverlayThemeEditor.DELETE, 100, 20, button -> minecraft.setScreen(new ConfirmScreen(delete -> {
                    if (delete) {
                        parent.removeTheme(template.id);
                        minecraft.setScreen(parent);
                    } else {
                        minecraft.setScreen(this);
                    }
                }, Component.translatable(Tl.Config.OverlayThemeEditor.DELETE_PROMPT, template.id), CommonComponents.EMPTY))));
        }

        return options;
    }

    @Override
    protected void renderForeground(GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        TooltipRenderer.render(ctx, partialTicks);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onClose() {
        if (cancelled) {
            TooltipRenderer.resetState();
            super.onClose();
            return;
        }

        if (idVal.getValue().isBlank()) {
            minecraft.getToasts().addToast(new SystemToast(
                SystemToast.SystemToastIds.TUTORIAL_HINT,
                Component.translatable(Tl.Config.MISSING_INPUT),
                Component.translatable(Tl.Config.OverlayThemeEditor.ID_EMPTY)));
        } else {
            ResourceLocation id = new ResourceLocation(idVal.getValue());
            if (id.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) && !idVal.getValue().startsWith(ResourceLocation.DEFAULT_NAMESPACE + ":")) {
                id = new ResourceLocation("custom", id.getPath());
            }

            parent.addTheme(new ThemeDefinition<>(id, type, false, type2attr.get(type)));
            TooltipRenderer.resetState();
            super.onClose();
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
        public float getScale() {
            return 2.0f;
        }

        @Override
        public Align.X getXAnchor() {
            return Align.X.CENTER;
        }

        @Override
        public Align.Y getYAnchor() {
            return Align.Y.TOP;
        }

        @Override
        public Align.X getXAlign() {
            return Align.X.CENTER;
        }

        @Override
        public Align.Y getYAlign() {
            return Align.Y.TOP;
        }

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 1;
        }

        @Override
        public boolean bossBarsOverlap() {
            return false;
        }

        @Override
        public int getBackgroundAlpha() {
            return 0xFF;
        }

        @Override
        public ITheme getTheme() {
            return theme;
        }

        @Override
        public boolean enableTextToSpeech() {
            return false;
        }

    }

}
