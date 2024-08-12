package mcp.mobius.waila.forge;

import java.nio.file.Path;
import java.util.Optional;

import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.PluginSide;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.ModInfo;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

public class ForgeCommonService implements ICommonService {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Optional<ModInfo> createModInfo(String namespace) {
        return ModList.get()
            .getModContainerById(namespace)
            .map(ModContainer::getModInfo)
            .map(data -> new ModInfo(true, data.getModId(), data.getDisplayName(), data.getVersion().getQualifier()));
    }

    @Override
    public boolean isDev() {
        return !FMLLoader.isProduction();
    }

    @Override
    public PluginSide getSide() {
        return switch (FMLLoader.getDist()) {
            case CLIENT -> PluginSide.CLIENT;
            case DEDICATED_SERVER -> PluginSide.DEDICATED_SERVER;
        };
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public String getIssueUrl() {
        return ModList.get().getModContainerById(WailaConstants.MOD_ID).get().getModInfo().getOwningFile().getConfig().<String>getConfigElement("issueTrackerURL").get();
    }

}
