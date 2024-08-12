package mcp.mobius.waila.quilt;

import java.nio.file.Path;
import java.util.Optional;

import mcp.mobius.waila.plugin.PluginSide;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.ModInfo;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;

public class QuiltCommonService implements ICommonService {

    @Override
    public String getPlatformName() {
        return "Quilt";
    }

    @Override
    public Path getGameDir() {
        return QuiltLoader.getGameDir();
    }

    @Override
    public Path getConfigDir() {
        return QuiltLoader.getConfigDir();
    }

    @Override
    public Optional<ModInfo> createModInfo(String namespace) {
        return QuiltLoader.getModContainer(namespace)
            .map(ModContainer::metadata)
            .map(data -> new ModInfo(true, data.id(), data.name(), data.version().raw()));
    }

    @Override
    public boolean isDev() {
        return QuiltLoader.isDevelopmentEnvironment();
    }

    @Override
    public PluginSide getSide() {
        return switch (MinecraftQuiltLoader.getEnvironmentType()) {
            case CLIENT -> PluginSide.CLIENT;
            case SERVER -> PluginSide.DEDICATED_SERVER;
        };
    }

}
