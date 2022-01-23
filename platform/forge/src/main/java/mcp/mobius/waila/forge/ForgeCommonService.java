package mcp.mobius.waila.forge;

import java.nio.file.Path;
import java.util.Optional;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.network.PacketSender;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

public class ForgeCommonService implements ICommonService {

    @Override
    public boolean isClientSide() {
        return FMLLoader.getDist() == Dist.CLIENT;
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
    public PacketSender getPacketSender() {
        return new ForgePacketSender();
    }

    @Override
    public Tag<Block> getBlockBlacklistTag() {
        return BlockTags.createOptional(Waila.id("blacklist"));
    }

    @Override
    public Tag<EntityType<?>> getEntityBlacklistTag() {
        return EntityTypeTags.createOptional(Waila.id("blacklist"));
    }

    @Override
    public Optional<ModInfo> createModInfo(String namespace) {
        return ModList.get()
            .getModContainerById(namespace)
            .map(data -> new ModInfo(data.getModId(), data.getModInfo().getDisplayName()));
    }

}
