package mcp.mobius.waila.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.config.input.KeyBind;
import mcp.mobius.waila.gui.hud.theme.ThemeDefinition;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class WailaConfig implements IWailaConfig {

    interface Nested {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface T {

        String value();

    }

    public static final Supplier<IJsonConfig.Commenter> COMMENTER = () -> {
        var defaultValue = new WailaConfig();
        var language = Language.getInstance();

        return path -> {
            if (path.isEmpty()) return null;

            AnnotatedElement element = null;
            Object value = defaultValue;
            Class<?> parentCls = WailaConfig.class;
            for (var part : path) {
                try {
                    var field = parentCls.getDeclaredField(part);
                    field.setAccessible(true);
                    value = field.get(value);

                    element = field;
                    parentCls = field.getType();
                } catch (NoSuchFieldException ignored) {
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            if (element == null) return null;
            if (value instanceof Nested) return null;

            var sb = new StringBuilder();

            var tlKey = element.getAnnotation(T.class);
            if (tlKey != null) {
                sb.append(language.getOrDefault(tlKey.value()));

                var descKey = tlKey.value() + "_desc";
                if (language.has(descKey)) sb.append('\n').append(language.getOrDefault(descKey));

                sb.append('\n');
            }

            if (value instanceof Enum<?> e) {
                sb.append(language.getOrDefault(Tl.Json5.Config.DEFAULT_VALUE).formatted(e.name()));

                var valuesSb = new StringBuilder();
                var enums = e.getDeclaringClass().getEnumConstants();
                valuesSb.append(enums[0].name());
                for (var i = 1; i < enums.length; i++) {
                    var anEnum = enums[i];
                    valuesSb.append(", ").append(anEnum.name());
                }
                sb.append("\n").append(language.getOrDefault(Tl.Json5.Config.AVAILABLE_VALUES).formatted(valuesSb));
            } else if (!(value instanceof Map<?, ?> || value instanceof Collection<?>)) {
                sb.append(language.getOrDefault(Tl.Json5.Config.DEFAULT_VALUE).formatted(value));
            }

            return sb.toString();
        };
    };

    private final General general = new General();
    private final Overlay overlay = new Overlay();

    @IJsonConfig.Comment("Text formatters")
    private final Formatter formatter = new Formatter();

    private final KeyBinds keyBinds = new KeyBinds();

    @IJsonConfig.Comment("Internal value, DO NOT TOUCH!")
    private int configVersion = 0;

    public int getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    @Override
    public General getGeneral() {
        return general;
    }

    @Override
    public Overlay getOverlay() {
        return overlay;
    }

    @Override
    public Formatter getFormatter() {
        return formatter;
    }

    public KeyBinds getKeyBinds() {
        return keyBinds;
    }

    public static class General implements IWailaConfig.General, Nested {

        private @T(Tl.Config.VANILLA_OPTIONS) boolean vanillaOptions = false;
        private @T(Tl.Config.DISPLAY_TOOLTIP) boolean displayTooltip = true;
        private @T(Tl.Config.SNEAKY_DETAILS) boolean shiftForDetails = false;
        private @T(Tl.Config.HIDE_SNEAK_TEXT) boolean hideShiftText = false;
        private @T(Tl.Config.DISPLAY_MODE) DisplayMode displayMode = DisplayMode.TOGGLE;
        private @T(Tl.Config.HIDE_FROM_PLAYERS) boolean hideFromPlayerList = true;
        private @T(Tl.Config.HIDE_FROM_DEBUG) boolean hideFromDebug = true;
        private @T(Tl.Config.TTS) boolean enableTextToSpeech = false;
        private @T(Tl.Config.RATE_LIMIT) int rateLimit = 250;

        public boolean vanillaOptions() {
            return vanillaOptions;
        }

        public void setVanillaOptions(boolean vanillaOptions) {
            this.vanillaOptions = vanillaOptions;
        }

        @Override
        public boolean isDisplayTooltip() {
            return displayTooltip;
        }

        public void setDisplayTooltip(boolean displayTooltip) {
            this.displayTooltip = displayTooltip;
        }

        @Override
        public boolean isShiftForDetails() {
            return shiftForDetails;
        }

        public void setShiftForDetails(boolean shiftForDetails) {
            this.shiftForDetails = shiftForDetails;
        }

        @Override
        public boolean isHideShiftText() {
            return hideShiftText;
        }

        public void setHideShiftText(boolean hideShiftText) {
            this.hideShiftText = hideShiftText;
        }

        @Override
        public DisplayMode getDisplayMode() {
            return displayMode;
        }

        public void setDisplayMode(DisplayMode displayMode) {
            this.displayMode = displayMode;
        }

        @Override
        public boolean isHideFromPlayerList() {
            return hideFromPlayerList;
        }

        public void setHideFromPlayerList(boolean hideFromPlayerList) {
            this.hideFromPlayerList = hideFromPlayerList;
        }

        @Override
        public boolean isHideFromDebug() {
            return hideFromDebug;
        }

        public void setHideFromDebug(boolean hideFromDebug) {
            this.hideFromDebug = hideFromDebug;
        }

        @Override
        public boolean isEnableTextToSpeech() {
            return enableTextToSpeech;
        }

        public void setEnableTextToSpeech(boolean enableTextToSpeech) {
            this.enableTextToSpeech = enableTextToSpeech;
        }

        @Override
        public int getRateLimit() {
            rateLimit = Math.max(rateLimit, 250);
            return rateLimit;
        }

        public void setRateLimit(int rateLimit) {
            this.rateLimit = rateLimit;
        }

    }

    public static class Overlay implements IWailaConfig.Overlay, Nested {

        private final Position position = new Position();
        private final Color color = new Color();

        private @T(Tl.Config.OVERLAY_SCALE) float scale = 1.0F;
        private @T(Tl.Config.OVERLAY_FPS) int fps = 30;

        @Override
        public Position getPosition() {
            return position;
        }

        @Override
        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        @Override
        public Color getColor() {
            return color;
        }

        public int getFps() {
            return fps;
        }

        public void setFps(int fps) {
            this.fps = fps;
        }

        public static class Position implements IWailaConfig.Overlay.Position, Nested {

            private final Align align = new Align();
            private final Align anchor = new Align();

            @T(Tl.Config.OVERLAY_OFFSET)
            private int x = 0;
            private int y = 0;

            private @T(Tl.Config.BOSS_BARS_OVERLAP) boolean bossBarsOverlap = false;

            @Override
            public int getX() {
                return x;
            }

            public void setX(int x) {
                this.x = x;
            }

            @Override
            public int getY() {
                return y;
            }

            public void setY(int y) {
                this.y = y;
            }

            @Override
            public Align getAlign() {
                return align;
            }

            @Override
            public Align getAnchor() {
                return anchor;
            }

            @Override
            public boolean isBossBarsOverlap() {
                return bossBarsOverlap;
            }

            public void setBossBarsOverlap(boolean bossBarsOverlap) {
                this.bossBarsOverlap = bossBarsOverlap;
            }

            public static class Align implements IWailaConfig.Overlay.Position.Align, Nested {

                X x = X.CENTER;
                Y y = Y.TOP;

                @Override
                public X getX() {
                    return x;
                }

                public void setX(X x) {
                    this.x = x;
                }

                @Override
                public Y getY() {
                    return y;
                }

                public void setY(Y y) {
                    this.y = y;
                }

            }

        }

        public static class Color implements IWailaConfig.Overlay.Color, Nested {

            private static final ResourceLocation DEFAULT = Waila.id("vanilla");

            private @T(Tl.Config.OVERLAY_BACKGROUND_ALPHA) int backgroundAlpha = 204;
            private @T(Tl.Config.OVERLAY_THEME) ResourceLocation activeTheme = DEFAULT;

            @IJsonConfig.Comment("Custom Themes")
            private final Map<ResourceLocation, ThemeDefinition<?>> themes = new HashMap<>();

            private ThemeDefinition<?> getThemeDef() {
                var allTheme = ThemeDefinition.getAll();

                if (!allTheme.containsKey(activeTheme)) {
                    activeTheme = DEFAULT;
                    WailaClient.CONFIG.save();
                }

                return allTheme.get(activeTheme);
            }

            @Override
            public int getBackgroundAlpha() {
                return backgroundAlpha;
            }

            public void setBackgroundAlpha(int backgroundAlpha) {
                this.backgroundAlpha = backgroundAlpha;
            }

            @Override
            public ITheme getTheme() {
                return getThemeDef().getInitializedInstance();
            }

            public Map<ResourceLocation, ThemeDefinition<?>> getCustomThemes() {
                return themes;
            }

            public ResourceLocation getActiveTheme() {
                return activeTheme;
            }

            public void applyTheme(ResourceLocation id) {
                var allTheme = ThemeDefinition.getAll();
                activeTheme = allTheme.containsKey(id) ? id : activeTheme;
            }

            public static class Adapter implements JsonSerializer<Color>, JsonDeserializer<Color> {

                @Override
                public Color deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    var json = element.getAsJsonObject();
                    var color = new Color();
                    color.backgroundAlpha = json.has("backgroundAlpha") ? json.getAsJsonPrimitive("backgroundAlpha").getAsInt() : 204;
                    color.activeTheme = new ResourceLocation(json.getAsJsonPrimitive("activeTheme").getAsString());
                    json.getAsJsonArray("themes").forEach(e -> {
                        ThemeDefinition<?> themeDef = context.deserialize(e, ThemeDefinition.class);
                        color.themes.put(themeDef.id, themeDef);
                    });
                    return color;
                }

                @Override
                public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
                    var json = new JsonObject();
                    json.addProperty("backgroundAlpha", src.backgroundAlpha);
                    json.add("themes", context.serialize(src.themes.values()));
                    json.addProperty("activeTheme", src.activeTheme.toString());
                    return json;
                }

            }

        }

    }

    public static class Formatter implements IWailaConfig.Formatter, Nested {

        private String modName = "§9§o%s";
        private String blockName = "§f%s";
        private String fluidName = "§f%s";
        private String entityName = "§f%s";
        private String registryName = "§8%s";

        private final Map<String, Style> styles = new ConcurrentHashMap<>();

        public String getModName() {
            return modName;
        }

        public void setModName(String modName) {
            styles.remove(this.modName);
            this.modName = modName;
        }

        public String getBlockName() {
            return blockName;
        }

        public void setBlockName(String blockName) {
            styles.remove(this.blockName);
            this.blockName = blockName;
        }

        public String getFluidName() {
            return fluidName;
        }

        public void setFluidName(String fluidName) {
            styles.remove(this.fluidName);
            this.fluidName = fluidName;
        }

        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            styles.remove(this.entityName);
            this.entityName = entityName;
        }

        public String getRegistryName() {
            return registryName;
        }

        public void setRegistryName(String registryName) {
            styles.remove(this.registryName);
            this.registryName = registryName;
        }

        private Component formatted(String format, Object object) {
            if (object instanceof Component component) {
                var style = styles.computeIfAbsent(format, f -> {
                    var ret = new Style[]{Style.EMPTY};
                    Component.literal(String.format(f, "B"))
                        .getVisualOrderText()
                        .accept(((i, s, j) -> {
                            ret[0] = s;
                            return true;
                        }));
                    return ret[0];
                });

                return component.copy().withStyle(style);
            }

            return Component.literal(String.format(format, object));
        }

        @Override
        public Component modName(Object modName) {
            return formatted(this.modName, modName);
        }

        @Override
        public Component blockName(Object blockName) {
            return formatted(this.blockName, blockName);
        }

        @Override
        public Component fluidName(Object fluidName) {
            return formatted(this.fluidName, fluidName);
        }

        @Override
        public Component entityName(Object entityName) {
            return formatted(this.entityName, entityName);
        }

        @Override
        public Component registryName(Object registryName) {
            return formatted(this.registryName, registryName);
        }

    }

    public static class KeyBinds implements Nested {

        private @T(Tl.Key.CONFIG) KeyBind openConfig = KeyBind.UNKNOWN;
        private @T(Tl.Key.SHOW_OVERLAY) KeyBind showOverlay = KeyBind.UNKNOWN;
        private @T(Tl.Key.TOGGLE_LIQUID) KeyBind toggleLiquid = KeyBind.UNKNOWN;
        private @T(Tl.Key.SHOW_RECIPE_INPUT) KeyBind showRecipeInput = KeyBind.UNKNOWN;
        private @T(Tl.Key.SHOW_RECIPE_OUTPUT) KeyBind showRecipeOutput = KeyBind.UNKNOWN;

        public KeyBind getOpenConfig() {
            return openConfig;
        }

        public void setOpenConfig(KeyBind openConfig) {
            this.openConfig = openConfig;
        }

        public KeyBind getShowOverlay() {
            return showOverlay;
        }

        public void setShowOverlay(KeyBind showOverlay) {
            this.showOverlay = showOverlay;
        }

        public KeyBind getToggleLiquid() {
            return toggleLiquid;
        }

        public void setToggleLiquid(KeyBind toggleLiquid) {
            this.toggleLiquid = toggleLiquid;
        }

        public KeyBind getShowRecipeInput() {
            return showRecipeInput;
        }

        public void setShowRecipeInput(KeyBind showRecipeInput) {
            this.showRecipeInput = showRecipeInput;
        }

        public KeyBind getShowRecipeOutput() {
            return showRecipeOutput;
        }

        public void setShowRecipeOutput(KeyBind showRecipeOutput) {
            this.showRecipeOutput = showRecipeOutput;
        }

    }

}
