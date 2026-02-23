package mcp.mobius.waila.paper;

import java.util.logging.Level;

import io.netty.buffer.Unpooled;
import mcp.mobius.waila.access.DataReader;
import mcp.mobius.waila.access.DataWriter;
import mcp.mobius.waila.access.ServerAccessor;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Handles incoming block and entity data request messages from the WTHIT client mod.
 * Deserializes request packets using NMS, runs the full IDataProvider pipeline to gather
 * data from all registered providers, and sends responses back via PaperPacketSender.
 * <p>
 * Important: These handlers must run on the main server thread because
 * {@code DataWriter.SERVER} and {@code DataReader.SERVER} are stateful singletons
 * that are not thread-safe. Bukkit's {@code PluginMessageListener} dispatches on
 * the main thread, which matches the threading model of the vanilla WTHIT mod.
 */
public class PaperDataHandler {

    private final Plugin plugin;

    public PaperDataHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles a block data request from the client.
     * <p>
     * Deserializes a {@link BlockHitResult} from the raw bytes, looks up the block entity
     * at the target position, runs all registered data providers, and sends the response
     * via the full DataWriter pipeline.
     */
    public void handleBlockRequest(Player player, byte[] message) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(message));
        try {
            BlockHitResult hitResult = buf.readBlockHitResult();

            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            var world = serverPlayer.level();
            var pos = hitResult.getBlockPos();

            //noinspection deprecation
            if (!world.hasChunkAt(pos)) {
                return;
            }

            var blockEntity = world.getBlockEntity(pos);
            if (blockEntity == null) {
                return;
            }

            var state = world.getBlockState(pos);
            var registrar = Registrar.get();
            var raw = DataWriter.SERVER.reset();
            IServerAccessor<BlockEntity> accessor = ServerAccessor.INSTANCE.set(world, serverPlayer, hitResult, blockEntity);

            for (var provider : registrar.blockData.get(blockEntity)) {
                DataWriter.SERVER.tryAppend(serverPlayer, provider.instance(), accessor, PluginConfig.SERVER, IDataProvider::appendData);
            }

            for (var provider : registrar.blockData.get(state.getBlock())) {
                DataWriter.SERVER.tryAppend(serverPlayer, provider.instance(), accessor, PluginConfig.SERVER, IDataProvider::appendData);
            }

            raw.putInt("x", pos.getX());
            raw.putInt("y", pos.getY());
            raw.putInt("z", pos.getZ());
            //noinspection ConstantConditions
            raw.putString("id", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString());
            raw.putLong("WailaTime", System.currentTimeMillis());

            var sender = new PaperPacketSender(plugin, player, serverPlayer);
            DataWriter.SERVER.send(sender, serverPlayer);
            DataReader.SERVER.reset(null);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to handle block data request from " + player.getName(), e);
        } finally {
            buf.release();
        }
    }

    /**
     * Handles an entity data request from the client.
     * <p>
     * Deserializes the entity id (VarInt) and hit position (3 doubles) from the raw bytes,
     * looks up the entity, runs all registered data providers, and sends the response
     * via the full DataWriter pipeline.
     */
    public void handleEntityRequest(Player player, byte[] message) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(message));
        try {
            int entityId = buf.readVarInt();
            double hitX = buf.readDouble();
            double hitY = buf.readDouble();
            double hitZ = buf.readDouble();

            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            var world = serverPlayer.level();

            Entity entity = world.getEntity(entityId);
            if (entity == null) {
                return;
            }

            var registrar = Registrar.get();
            var raw = DataWriter.SERVER.reset();
            IServerAccessor<Entity> accessor = ServerAccessor.INSTANCE.set(world, serverPlayer, new EntityHitResult(entity, new Vec3(hitX, hitY, hitZ)), entity);

            for (var provider : registrar.entityData.get(entity)) {
                DataWriter.SERVER.tryAppend(serverPlayer, provider.instance(), accessor, PluginConfig.SERVER, IDataProvider::appendData);
            }

            raw.putInt("WailaEntityID", entity.getId());
            raw.putLong("WailaTime", System.currentTimeMillis());

            var sender = new PaperPacketSender(plugin, player, serverPlayer);
            DataWriter.SERVER.send(sender, serverPlayer);
            DataReader.SERVER.reset(null);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to handle entity data request from " + player.getName(), e);
        } finally {
            buf.release();
        }
    }

}
