package mcp.mobius.waila.fabric;

import java.nio.file.Path;
import java.util.Optional;

import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.PluginSide;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.ModInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class FabricCommonService implements ICommonService {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Optional<ModInfo> createModInfo(String namespace) {
        return FabricLoader.getInstance().getModContainer(namespace)
            .map(ModContainer::getMetadata)
            .map(data -> new ModInfo(true, data.getId(), data.getName(), data.getVersion().getFriendlyString()));
    }

    @Override
    public boolean isDev() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public PluginSide getSide() {
        return switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT -> PluginSide.CLIENT;
            case SERVER -> PluginSide.DEDICATED_SERVER;
        };
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public String getIssueUrl() {
        return FabricLoader.getInstance().getModContainer(WailaConstants.MOD_ID).get().getMetadata().getContact().get("issues").get();
    }

}
