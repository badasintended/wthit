package mcp.mobius.waila.network;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.google.gson.Gson;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.data.DataAccessor;
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

    public static void requestEntity(ServerPlayer player, int entityId, Consumer<CompoundTag> consumer) {
        Registrar registrar = Registrar.INSTANCE;
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
        Registrar registrar = Registrar.INSTANCE;
        Level world = player.level;
        //noinspection deprecation
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
        //noinspection ConstantConditions
        tag.putString("id", Registry.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString());

        consumer.accept(tag);
    }

    private static <T> void setBlackList(int[] ids, Set<T> set, Registry<T> registry) {
        for (int id : ids) {
            set.add(registry.byId(id));
        }
    }

}
