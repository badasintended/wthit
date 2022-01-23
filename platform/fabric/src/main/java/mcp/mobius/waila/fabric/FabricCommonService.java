package mcp.mobius.waila.fabric;

import java.nio.file.Path;
import java.util.Optional;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.network.PacketSender;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.ModInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public class FabricCommonService implements ICommonService {

    @Override
    public boolean isClientSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
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
    public PacketSender getPacketSender() {
        return new FabricPacketSender();
    }

    @Override
    public Tag<Block> getBlockBlacklistTag() {
        return TagFactory.BLOCK.create(Waila.id("blacklist"));
    }

    @Override
    public Tag<EntityType<?>> getEntityBlacklistTag() {
        return TagFactory.ENTITY_TYPE.create(Waila.id("blacklist"));
    }

    @Override
    public Optional<ModInfo> createModInfo(String namespace) {
        return FabricLoader.getInstance().getModContainer(namespace)
            .map(data -> new ModInfo(data.getMetadata().getId(), data.getMetadata().getName()));
    }

}
