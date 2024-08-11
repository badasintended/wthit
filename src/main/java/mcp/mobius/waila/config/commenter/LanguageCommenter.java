package mcp.mobius.waila.config.commenter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.locale.Language;
import org.jetbrains.annotations.Nullable;

public class LanguageCommenter implements IJsonConfig.Commenter {

    private final Map<String, String> translation;
    private final Impl impl;

    public LanguageCommenter(Impl impl) {
        this.impl = impl;
        translation = new HashMap<>();
        try (var stream = PluginConfig.class.getResourceAsStream("/assets/waila/lang/en_us.json")) {
            Language.loadFromJson(Objects.requireNonNull(stream), translation::put);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable String getComment(List<String> path) {
        return impl.getComment(translation, path);
    }

    public interface Impl {

        @Nullable
        String getComment(Map<String, String> translation, List<String> path);

    }

}
