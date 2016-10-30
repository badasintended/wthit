package mcp.mobius.waila.config;

import com.google.gson.*;
import mcp.mobius.waila.Waila;
import net.minecraft.client.gui.GuiTextField;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ColorConfig {

    public static final List<ColorConfig> ACTIVE_CONFIGS = new ArrayList<ColorConfig>();

    private static final ColorConfig DEFAULT_VANILLA = new ColorConfig("cfg.theme.vanilla", null, null, null, null);
    private static final ColorConfig DEFAULT_DARK = new ColorConfig("cfg.theme.dark", Color.decode("#131313"), Color.decode("#383838"), Color.decode("#242424"), null);

    public static void init() {
        try {
            if (!Waila.themeDir.exists() && Waila.themeDir.mkdirs()) {
                String vanillaJson = OverlayConfig.GSON.toJson(DEFAULT_VANILLA);
                FileWriter vanillaWriter = new FileWriter(new File(Waila.themeDir, "vanilla.json"));
                vanillaWriter.write(vanillaJson);
                vanillaWriter.close();

                String darkJson = OverlayConfig.GSON.toJson(DEFAULT_DARK);
                FileWriter darkWriter = new FileWriter(new File(Waila.themeDir, "dark.json"));
                darkWriter.write(darkJson);
                darkWriter.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        File[] themeFiles = Waila.themeDir.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".json"));
        if (themeFiles != null) {
            try {
                for (File file : themeFiles)
                    ACTIVE_CONFIGS.add(OverlayConfig.GSON.fromJson(new FileReader(file), ColorConfig.class));
            } catch (Exception e) {
                Waila.LOGGER.error("Error parsing theme files");
            }
        }

        // List Vanilla theme first
        Collections.sort(ACTIVE_CONFIGS, new Comparator<ColorConfig>() {
            @Override
            public int compare(ColorConfig config1, ColorConfig config2) {
                return config1.getName().equalsIgnoreCase("cfg.theme.vanilla") ? 1 : 0;
            }
        });
    }

    private final String name;
    private final Color background;
    private final Color gradientTop;
    private final Color gradientBottom;
    private final Color font;

    public ColorConfig(String name, Color background, Color gradientTop, Color gradientBottom, Color font) {
        this.name = name;
        this.background = background;
        this.gradientTop = gradientTop;
        this.gradientBottom = gradientBottom;
        this.font = font;
    }

    public String getName() {
        return name != null ? name : RandomStringUtils.random(10);
    }

    public Color getBackground() {
        return background;
    }

    public Color getGradientTop() {
        return gradientTop;
    }

    public Color getGradientBottom() {
        return gradientBottom;
    }

    public Color getFont() {
        return font;
    }

    public void apply(GuiTextField background, GuiTextField gradientTop, GuiTextField gradientBottom, GuiTextField font) {
        background.setText(OverlayConfig.toHex(getBackground()));
        gradientTop.setText(OverlayConfig.toHex(getGradientTop()));
        gradientBottom.setText(OverlayConfig.toHex(getGradientBottom()));
        font.setText(OverlayConfig.toHex(getFont()));
    }

    public static class Serializer implements JsonSerializer<ColorConfig>, JsonDeserializer<ColorConfig> {

        @Override
        public ColorConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String name = json.getAsJsonObject().get("name").getAsString();
            String background = getOptionalString(json, "background", "#100010");
            String gradientTop = getOptionalString(json, "gradientTop", "#5000FF");
            String gradientBottom = getOptionalString(json, "gradientBottom", "#28007F");
            String font = getOptionalString(json, "font", "#A0A0A0");
            return new ColorConfig(name, Color.decode(background), Color.decode(gradientTop), Color.decode(gradientBottom), Color.decode(font));
        }

        @Override
        public JsonElement serialize(ColorConfig src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("name", src.getName() != null ? src.getName() : RandomStringUtils.random(10));
            jsonObject.addProperty("background", src.getBackground() != null ? OverlayConfig.toHex(src.getBackground()) : "#100010");
            jsonObject.addProperty("gradientTop", src.getGradientTop() != null ? OverlayConfig.toHex(src.getGradientTop()) : "#5000FF");
            jsonObject.addProperty("gradientBottom", src.getGradientBottom() != null ? OverlayConfig.toHex(src.getGradientBottom()): "#28007F");
            jsonObject.addProperty("font", src.getFont() != null ? OverlayConfig.toHex(src.getFont()) : "#A0A0A0");
            return jsonObject;
        }

        private String getOptionalString(JsonElement jsonElement, String memberName, String default_) {
            if (jsonElement.getAsJsonObject().has(memberName))
                return jsonElement.getAsJsonObject().get(memberName).getAsString();

            return default_;
        }
    }
}
