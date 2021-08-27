package mcp.mobius.waila.config;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import mcp.mobius.waila.api.IWailaConfig;
import net.minecraft.resources.ResourceLocation;

public class WailaConfig implements IWailaConfig {

    private final General general = new General();
    private final Overlay overlay = new Overlay();
    private final Formatting formatting = new Formatting();
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
    public Formatting getFormatting() {
        return formatting;
    }

    public static class General implements IWailaConfig.General {

        private boolean displayTooltip = true;
        private boolean shiftForDetails = false;
        private DisplayMode displayMode = DisplayMode.TOGGLE;
        private boolean hideFromPlayerList = true;
        private boolean hideFromDebug = true;
        private boolean enableTextToSpeech = false;
        private int rateLimit = 250;
        private int maxHealthForRender = 40;
        private int maxHeartsPerLine = 10;

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

        @Override
        public int getMaxHealthForRender() {
            return maxHealthForRender;
        }

        public void setMaxHealthForRender(int maxHealthForRender) {
            this.maxHealthForRender = maxHealthForRender;
        }

        @Override
        public int getMaxHeartsPerLine() {
            return maxHeartsPerLine;
        }

        public void setMaxHeartsPerLine(int maxHeartsPerLine) {
            this.maxHeartsPerLine = maxHeartsPerLine;
        }

    }

    public static class Overlay implements IWailaConfig.Overlay {

        private final Position position = new Position();
        private final Color color = new Color();
        private float scale = 1.0F;

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

        public static class Position implements IWailaConfig.Overlay.Position {

            private final Align align = new Align();
            private final Align anchor = new Align();
            private int x = 0;
            private int y = 0;

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

            public static class Align implements IWailaConfig.Overlay.Position.Align {

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

        public static class Color implements IWailaConfig.Overlay.Color {

            private int alpha = 80;
            private Map<ResourceLocation, Theme> themes = new HashMap<>();
            private ResourceLocation activeTheme = Theme.VANILLA.getId();

            public Color() {
                themes.put(Theme.VANILLA.getId(), Theme.VANILLA);
                themes.put(Theme.DARK.getId(), Theme.DARK);
            }

            @Override
            public int getAlpha() {
                return alpha == 100 ? 255 : alpha == 0 ? (int) (0.4F / 100.0F * 256) << 24 : (int) (alpha / 100.0F * 256) << 24;
            }

            public void setAlpha(int alpha) {
                this.alpha = alpha;
            }

            public int rawAlpha() {
                return alpha;
            }

            public Theme theme() {
                return themes.getOrDefault(activeTheme, Theme.VANILLA);
            }

            public Collection<Theme> themes() {
                return themes.values();
            }

            @Override
            public int getBackgroundColor() {
                return getAlpha() + theme().getBackgroundColor();
            }

            @Override
            public int getGradientStart() {
                return getAlpha() + theme().getGradientStart();
            }

            @Override
            public int getGradientEnd() {
                return getAlpha() + theme().getGradientEnd();
            }

            @Override
            public int getFontColor() {
                return theme().getFontColor();
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
                    color.themes = new HashMap<>();
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

    public static class Formatting implements IWailaConfig.Formatting {

        private String modName = "\u00A79\u00A7o%s";
        private String blockName = "\u00a7f%s";
        private String fluidName = "\u00a7f%s";
        private String entityName = "\u00a7f%s";
        private String registryName = "\u00a78%s";

        @Override
        public String getModName() {
            return modName;
        }

        public void setModName(String modName) {
            this.modName = modName;
        }

        @Override
        public String getBlockName() {
            return blockName;
        }

        public void setBlockName(String blockName) {
            this.blockName = blockName;
        }

        @Override
        public String getFluidName() {
            return fluidName;
        }

        public void setFluidName(String fluidName) {
            this.fluidName = fluidName;
        }

        @Override
        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        @Override
        public String getRegistryName() {
            return registryName;
        }

        public void setRegistryName(String registryName) {
            this.registryName = registryName;
        }

    }

}
