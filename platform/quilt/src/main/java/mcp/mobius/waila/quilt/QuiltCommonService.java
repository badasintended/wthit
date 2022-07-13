package mcp.mobius.waila.quilt;

import java.nio.file.Path;
import java.util.Optional;

import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.ModInfo;
import net.fabricmc.api.EnvType;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;

public class QuiltCommonService implements ICommonService {

    @Override
    public boolean isClientSide() {
        return MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT;
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
            .map(mod -> new ModInfo(mod.metadata().id(), mod.metadata().name()));
    }

}
