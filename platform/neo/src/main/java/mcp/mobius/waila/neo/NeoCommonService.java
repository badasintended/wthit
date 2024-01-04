package mcp.mobius.waila.neo;

import java.nio.file.Path;
import java.util.Optional;

import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.ModInfo;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

public class NeoCommonService implements ICommonService {

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
        return ((Optional<ModContainer>) ModList.get().getModContainerById(namespace))
            .or(() -> ModList.get().getModContainerById(namespace.replace('_', '-')))
            .map(ModContainer::getModInfo)
            .map(data -> new ModInfo(true, data.getModId(), data.getDisplayName(), data.getVersion().getQualifier()));
    }

    @Override
    public boolean isDev() {
        return !FMLLoader.isProduction();
    }

    @Override
    public IPluginInfo.Side getSide() {
        return switch (FMLLoader.getDist()) {
            case CLIENT -> IPluginInfo.Side.CLIENT;
            case DEDICATED_SERVER -> IPluginInfo.Side.SERVER;
        };
    }

}
