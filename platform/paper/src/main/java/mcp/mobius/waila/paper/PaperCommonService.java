package mcp.mobius.waila.paper;

import java.nio.file.Path;
import java.util.Optional;

import mcp.mobius.waila.plugin.PluginSide;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.ModInfo;
import org.bukkit.Bukkit;

public class PaperCommonService implements ICommonService {

    @Override
    public String getPlatformName() {
        return "Paper";
    }

    @Override
    public Path getGameDir() {
        return Path.of(".");
    }

    @Override
    public Path getConfigDir() {
        return Path.of("plugins/wthit");
    }

    @Override
    public Optional<ModInfo> createModInfo(String namespace) {
        if (namespace.equals("wthit") || namespace.equals("waila")) {
            var plugin = Bukkit.getPluginManager().getPlugin("wthit");
            if (plugin != null) {
                return Optional.of(new ModInfo(true, "wthit", plugin.getName(), plugin.getDescription().getVersion()));
            }
        }

        if (namespace.equals("minecraft")) {
            return Optional.of(new ModInfo(true, "minecraft", "Minecraft", Bukkit.getMinecraftVersion()));
        }

        return Optional.empty();
    }

    @Override
    public boolean isDev() {
        return false;
    }

    @Override
    public PluginSide getSide() {
        return PluginSide.DEDICATED_SERVER;
    }

    @Override
    public String getIssueUrl() {
        return "https://github.com/badasintended/wthit/issues";
    }

}
