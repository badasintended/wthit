package mcp.mobius.waila.network;

import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.Gson;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.data.DataAccessor;
import mcp.mobius.waila.registry.TooltipRegistrar;
import mcp.mobius.waila.util.CommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PacketExecutor {

    private static final Gson GSON = new Gson();

    public static void receiveData(CompoundTag tag) {
        DataAccessor.INSTANCE.setServerData(tag);
    }

    public static void sendConfig(Map<ResourceLocation, Boolean> map) {
        PluginConfig.INSTANCE.getSyncableConfigs().forEach(config ->
            config.setValue(map.getOrDefault(config.getId(), config.getDefaultValue())));
        CommonUtil.LOGGER.info("Received config from the server: {}", GSON.toJson(map));
    }

    public static void requestEntity(ServerPlayer player, int entityId, Consumer<CompoundTag> consumer) {
        TooltipRegistrar registrar = TooltipRegistrar.INSTANCE;
        Level world = player.level;
        Entity entity = world.getEntity(entityId);

        if (entity == null)
            return;

        CompoundTag tag = new CompoundTag();
        registrar.entityData.get(entity).forEach(provider ->
            provider.appendServerData(tag, player, world, entity)
        );

        tag.putInt("WailaEntityID", entity.getId());
        consumer.accept(tag);
    }

    public static void requestBlockEntity(ServerPlayer player, BlockPos pos, Consumer<CompoundTag> consumer) {
        TooltipRegistrar registrar = TooltipRegistrar.INSTANCE;
        Level world = player.level;
        if (!world.hasChunkAt(pos))
            return;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null)
            return;

        BlockState state = world.getBlockState(pos);

        CompoundTag tag = new CompoundTag();
        registrar.blockData.get(blockEntity).forEach(provider ->
            provider.appendServerData(tag, player, world, blockEntity));
        registrar.blockData.get(state.getBlock()).forEach(provider ->
            provider.appendServerData(tag, player, world, blockEntity));

        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        tag.putString("id", Registry.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString());

        consumer.accept(tag);
    }

}
