package mcp.mobius.waila.service;

import java.nio.file.Path;
import java.util.Optional;

import mcp.mobius.waila.impl.Impl;
import mcp.mobius.waila.network.PacketSender;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public interface ICommonService {

    ICommonService INSTANCE = Impl.loadService(ICommonService.class);

    boolean isClientSide();

    Path getGameDir();

    Path getConfigDir();

    PacketSender getPacketSender();

    Tag<Block> getBlockBlacklistTag();

    Tag<EntityType<?>> getEntityBlacklistTag();

    Optional<ModInfo> createModInfo(String namespace);

}
