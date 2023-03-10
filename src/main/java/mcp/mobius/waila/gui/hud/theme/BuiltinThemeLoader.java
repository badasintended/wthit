package mcp.mobius.waila.gui.hud.theme;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import mcp.mobius.waila.Waila;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

public class BuiltinThemeLoader extends SimpleJsonResourceReloadListener {

    public static final Map<ResourceLocation, ThemeDefinition<?>> THEMES = new HashMap<>();

    protected static final ResourceLocation ID = Waila.id("builtin_themes");

    public BuiltinThemeLoader() {
        super(new Gson(), "waila_themes");
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        THEMES.clear();

        map.forEach((id, json) -> {
            try {
                Waila.CONFIG.get().getOverlay().getColor().getCustomThemes().remove(id);
                THEMES.put(id, ThemeDefinition.Adapter.deserialize(id, json, true));
            } catch (Exception e) {
                Waila.LOGGER.error("Couldn't parse builtin theme definition {}", id, e);
            }
        });

        Waila.CONFIG.save();
    }

}
