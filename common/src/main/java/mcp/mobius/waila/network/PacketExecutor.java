package mcp.mobius.waila.network;

import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.Gson;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.Registrar;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class PacketExecutor {

    private static final Gson GSON = new Gson();

    public static void receiveData(CompoundTag tag) {
        DataAccessor.INSTANCE.setServerData(tag);
    }

    public static void sendConfig(Map<Identifier, Boolean> map) {
        map.forEach(PluginConfig.INSTANCE::set);
        Waila.LOGGER.info("Received config from the server: {}", GSON.toJson(map));
    }

    public static void requestEntity(ServerPlayerEntity player, int entityId, Consumer<CompoundTag> consumer) {
        Registrar registrar = Registrar.INSTANCE;
        World world = player.world;
        Entity entity = world.getEntityById(entityId);

        if (entity == null)
            return;

        CompoundTag tag = new CompoundTag();
        registrar.getEntityData(entity).forEach(provider ->
            provider.get().appendServerData(tag, player, world, (LivingEntity) entity)
        );

        tag.putInt("WailaEntityID", entity.getEntityId());
        consumer.accept(tag);
    }

    public static void requestBlockEntity(ServerPlayerEntity player, BlockPos pos, Consumer<CompoundTag> consumer) {
        Registrar registrar = Registrar.INSTANCE;
        World world = player.world;
        if (!world.isChunkLoaded(pos))
            return;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null)
            return;

        BlockState state = world.getBlockState(pos);

        CompoundTag tag = new CompoundTag();
        registrar.getBlockData(blockEntity).forEach(provider ->
            provider.get().appendServerData(tag, player, world, blockEntity));
        registrar.getBlockData(state.getBlock()).forEach(provider ->
            provider.get().appendServerData(tag, player, world, blockEntity));

        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        tag.putString("id", Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType()).toString());

        consumer.accept(tag);
    }

}
