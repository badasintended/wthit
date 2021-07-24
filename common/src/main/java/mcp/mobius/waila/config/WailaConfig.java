package mcp.mobius.waila.config;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;

public class WailaConfig {

    private int configVersion = 0;

    private final General general = new General();
    private final Overlay overlay = new Overlay();
    private final Formatting formatting = new Formatting();

    public int getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    public General getGeneral() {
        return general;
    }

    public Overlay getOverlay() {
        return overlay;
    }

    public Formatting getFormatting() {
        return formatting;
    }

    public static class General {

        private boolean displayTooltip = true;
        private boolean shiftForDetails = false;
        private DisplayMode displayMode = DisplayMode.TOGGLE;
        private boolean hideFromPlayerList = true;
        private boolean hideFromDebug = true;
        private boolean enableTextToSpeech = false;
        private int rateLimit = 250;
        private int maxHealthForRender = 40;
        private int maxHeartsPerLine = 10;

        public void setDisplayTooltip(boolean displayTooltip) {
            this.displayTooltip = displayTooltip;
        }

        public void setShiftForDetails(boolean shiftForDetails) {
            this.shiftForDetails = shiftForDetails;
        }

        public void setDisplayMode(DisplayMode displayMode) {
            this.displayMode = displayMode;
        }

        public void setHideFromPlayerList(boolean hideFromPlayerList) {
            this.hideFromPlayerList = hideFromPlayerList;
        }

        public void setHideFromDebug(boolean hideFromDebug) {
            this.hideFromDebug = hideFromDebug;
        }

        public void setEnableTextToSpeech(boolean enableTextToSpeech) {
            this.enableTextToSpeech = enableTextToSpeech;
        }

        public void setRateLimit(int rateLimit) {
            this.rateLimit = rateLimit;
        }

        public void setMaxHealthForRender(int maxHealthForRender) {
            this.maxHealthForRender = maxHealthForRender;
        }

        public void setMaxHeartsPerLine(int maxHeartsPerLine) {
            this.maxHeartsPerLine = maxHeartsPerLine;
        }

        public boolean shouldDisplayTooltip() {
            return displayTooltip;
        }

        public boolean shouldShiftForDetails() {
            return shiftForDetails;
        }

        public DisplayMode getDisplayMode() {
            return displayMode;
        }

        public boolean shouldHideFromPlayerList() {
            return hideFromPlayerList;
        }

        public boolean shouldHideFromDebug() {
            return hideFromDebug;
        }

        public boolean shouldEnableTextToSpeech() {
            return enableTextToSpeech;
        }

        public int getRateLimit() {
            rateLimit = Math.max(rateLimit, 250);
            return rateLimit;
        }

        public int getMaxHealthForRender() {
            return maxHealthForRender;
        }

        public int getMaxHeartsPerLine() {
            return maxHeartsPerLine;
        }

        public enum DisplayMode {
            HOLD_KEY,
            TOGGLE
        }

    }

    public static class Overlay {

        private final Position position = new Position();
        private float scale = 1.0F;
        private final Color color = new Color();

        public void setScale(float scale) {
            this.scale = scale;
        }

        public Position getPosition() {
            return position;
        }

        public float getScale() {
            return scale;
        }

        public Color getColor() {
            return color;
        }

        public static class Position {

            private int x = 0;
            private int y = 0;
            private X alignX = X.CENTER;
            private Y alignY = Y.TOP;
            private X anchorX = X.CENTER;
            private Y anchorY = Y.TOP;

            public void setX(int x) {
                this.x = x;
            }

            public void setY(int y) {
                this.y = y;
            }

            public void setAlignX(X alignX) {
                this.alignX = alignX;
            }

            public void setAlignY(Y alignY) {
                this.alignY = alignY;
            }

            public void setAnchorX(X anchorX) {
                this.anchorX = anchorX;
            }

            public void setAnchorY(Y anchorY) {
                this.anchorY = anchorY;
            }

            public int getX() {
                return x;
            }

            public int getY() {
                return y;
            }

            public X getAlignX() {
                return alignX;
            }

            public Y getAlignY() {
                return alignY;
            }

            public X getAnchorX() {
                return anchorX;
            }

            public Y getAnchorY() {
                return anchorY;
            }

            public enum X {
                LEFT(0.0),
                CENTER(0.5),
                RIGHT(1.0);

                public final double multiplier;

                X(double multiplier) {
                    this.multiplier = multiplier;
                }
            }

            public enum Y {
                TOP(0.0),
                MIDDLE(0.5),
                BOTTOM(1.0);

                public final double multiplier;
                Y(double multiplier) {
                    this.multiplier = multiplier;
                }

            }

        }

        public static class Color {

            private int alpha = 80;
            private Map<ResourceLocation, Theme> themes = Maps.newHashMap();
            private ResourceLocation activeTheme = Theme.VANILLA.getId();

            public Color() {
                themes.put(Theme.VANILLA.getId(), Theme.VANILLA);
                themes.put(Theme.DARK.getId(), Theme.DARK);
            }

            public int getAlpha() {
                return alpha == 100 ? 255 : alpha == 0 ? (int) (0.4F / 100.0F * 256) << 24 : (int) (alpha / 100.0F * 256) << 24;
            }

            public int getRawAlpha() {
                return alpha;
            }

            public Theme getTheme() {
                return themes.getOrDefault(activeTheme, Theme.VANILLA);
            }

            public Collection<Theme> getThemes() {
                return themes.values();
            }

            public void setAlpha(int alpha) {
                this.alpha = alpha;
            }

            public int getBackgroundColor() {
                return getAlpha() + getTheme().getBackgroundColor();
            }

            public int getGradientStart() {
                return getAlpha() + getTheme().getGradientStart();
            }

            public int getGradientEnd() {
                return getAlpha() + getTheme().getGradientEnd();
            }

            public int getFontColor() {
                return getTheme().getFontColor();
            }

            public void applyTheme(ResourceLocation id) {
                activeTheme = themes.containsKey(id) ? id : activeTheme;
            }

            public static class Adapter implements JsonSerializer<Color>, JsonDeserializer<Color> {

                @Override
                public Color deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    JsonObject json = element.getAsJsonObject();
                    Color color = new Color();
                    color.alpha = json.getAsJsonPrimitive("alpha").getAsInt();
                    color.activeTheme = new ResourceLocation(json.getAsJsonPrimitive("activeTheme").getAsString());
                    color.themes = Maps.newHashMap();
                    json.getAsJsonArray("themes").forEach(e -> {
                        Theme theme = context.deserialize(e, Theme.class);
                        color.themes.put(theme.getId(), theme);
                    });
                    return color;
                }

                @Override
                public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
                    JsonObject json = new JsonObject();
                    json.addProperty("alpha", src.alpha);
                    json.add("themes", context.serialize(src.themes.values()));
                    json.addProperty("activeTheme", src.activeTheme.toString());
                    return json;
                }

            }

        }

    }

    public static class Formatting {

        private String modName = "\u00A79\u00A7o%s";
        private String blockName = "\u00a7f%s";
        private String fluidName = "\u00a7f%s";
        private String entityName = "\u00a7f%s";
        private String registryName = "\u00a78%s";

        public void setModName(String modName) {
            this.modName = modName;
        }

        public void setBlockName(String blockName) {
            this.blockName = blockName;
        }

        public void setFluidName(String fluidName) {
            this.fluidName = fluidName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        public void setRegistryName(String registryName) {
            this.registryName = registryName;
        }

        public String getModName() {
            return modName;
        }

        public String getBlockName() {
            return blockName;
        }

        public String getFluidName() {
            return fluidName;
        }

        public String getEntityName() {
            return entityName;
        }

        public String getRegistryName() {
            return registryName;
        }

    }

}
