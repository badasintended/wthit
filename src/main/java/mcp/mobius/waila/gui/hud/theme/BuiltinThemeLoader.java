package mcp.mobius.waila.gui.hud.theme;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.util.Log;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

public class BuiltinThemeLoader extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {

    private static final Log LOG = Log.create();

    public static final Map<Identifier, ThemeDefinition<?>> THEMES = new HashMap<>();
    public static final Identifier ID = Waila.id("builtin_themes");

    public BuiltinThemeLoader() {
    }

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        var map = new HashMap<Identifier, JsonElement>();
        var fileToIdConverter = FileToIdConverter.json("waila_themes");

        for (var entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
            var resourceLocation = entry.getKey();
            var resourceLocation2 = fileToIdConverter.fileToId(resourceLocation);

            try (var reader = entry.getValue().openAsReader()) {
                var json = JsonParser.parseReader(reader);
                map.put(resourceLocation2, json);
            } catch (IOException e) {
                LOG.error("Couldn't parse data file '{}' from '{}'", resourceLocation2, resourceLocation, e);
            }
        }

        return map;
    }

    @Override
    protected void apply(@NotNull Map<Identifier, JsonElement> map, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        THEMES.clear();

        map.forEach((id, json) -> {
            try {
                WailaClient.CONFIG.get().getOverlay().getColor().getCustomThemes().remove(id);
                THEMES.put(id, ThemeDefinition.Adapter.deserialize(id, json, true));
            } catch (Exception e) {
                LOG.error("Couldn't parse builtin theme definition {}", id, e);
            }
        });

        WailaClient.CONFIG.save();
    }

}
