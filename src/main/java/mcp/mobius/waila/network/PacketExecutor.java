package mcp.mobius.waila.network;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataAccessor;
import mcp.mobius.waila.access.ServerAccessor;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PacketExecutor {

    private static final Gson GSON = new Gson();

    @SuppressWarnings("ConstantConditions")
    public static void generateClientDump() {
        Path path = Waila.GAME_DIR.resolve(".waila/WailaClientDump.md");
        if (DumpGenerator.generate(path)) {
            Minecraft.getInstance().player.displayClientMessage(new TranslatableComponent("command.waila.client_dump_success", path), false);
        }
    }

    public static void receiveData(CompoundTag tag) {
        DataAccessor.INSTANCE.setServerData(tag);
    }

    public static void sendConfig(Map<ResourceLocation, Object> map) {
        for (ConfigEntry<Object> config : PluginConfig.INSTANCE.getSyncableConfigs()) {
            ResourceLocation id = config.getId();
            Object defaultValue = config.getDefaultValue();
            Object syncedValue = defaultValue instanceof Enum<?> e
                ? Enum.valueOf(e.getDeclaringClass(), (String) map.getOrDefault(id, e.name()))
                : map.getOrDefault(id, defaultValue);
            config.setValue(syncedValue);
        }
        Waila.LOGGER.info("Received config from the server: {}", GSON.toJson(map));
    }

    public static void sendBlacklist(int[][] rawIds) {
        BlacklistConfig blacklist = Waila.BLACKLIST_CONFIG.get();
        setBlackList(rawIds[0], blacklist.blocks, Registry.BLOCK);
        setBlackList(rawIds[1], blacklist.blockEntityTypes, Registry.BLOCK_ENTITY_TYPE);
        setBlackList(rawIds[2], blacklist.entityTypes, Registry.ENTITY_TYPE);
    }

    public static void requestEntity(ServerPlayer player, IntObjectPair<Vec3> pair, Consumer<CompoundTag> consumer) {
        Registrar registrar = Registrar.INSTANCE;
        Level world = player.level;
        Entity entity = world.getEntity(pair.leftInt());

        if (entity == null) {
            return;
        }

        CompoundTag data = new CompoundTag();
        IServerAccessor<Entity> accessor = ServerAccessor.INSTANCE.set(world, player, new EntityHitResult(entity, pair.right()), entity);

        for (IServerDataProvider<Entity> provider : registrar.entityData.get(entity)) {
            provider.appendServerData(data, accessor, PluginConfig.INSTANCE);
            provider.appendServerData(data, player, world, entity);
        }

        data.putInt("WailaEntityID", entity.getId());
        consumer.accept(data);
    }

    public static void requestBlockEntity(ServerPlayer player, BlockHitResult hitResult, Consumer<CompoundTag> consumer) {
        Registrar registrar = Registrar.INSTANCE;
        Level world = player.level;
        BlockPos pos = hitResult.getBlockPos();

        //noinspection deprecation
        if (!world.hasChunkAt(pos)) {
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return;
        }

        BlockState state = world.getBlockState(pos);
        CompoundTag data = new CompoundTag();
        IServerAccessor<BlockEntity> accessor = ServerAccessor.INSTANCE.set(world, player, hitResult, blockEntity);

        for (IServerDataProvider<BlockEntity> provider : registrar.blockData.get(blockEntity)) {
            provider.appendServerData(data, accessor, PluginConfig.INSTANCE);
            provider.appendServerData(data, player, world, blockEntity);
        }

        for (IServerDataProvider<BlockEntity> provider : registrar.blockData.get(state.getBlock())) {
            provider.appendServerData(data, accessor, PluginConfig.INSTANCE);
            provider.appendServerData(data, player, world, blockEntity);
        }

        data.putInt("x", pos.getX());
        data.putInt("y", pos.getY());
        data.putInt("z", pos.getZ());
        //noinspection ConstantConditions
        data.putString("id", Registry.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString());

        consumer.accept(data);
    }

    private static <T> void setBlackList(int[] ids, Set<T> set, Registry<T> registry) {
        for (int id : ids) {
            set.add(registry.byId(id));
        }
    }

}
