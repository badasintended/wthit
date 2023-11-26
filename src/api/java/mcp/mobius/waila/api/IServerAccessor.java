package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ServerOnly
@ApiStatus.NonExtendable
public interface IServerAccessor<T> {

    Level getWorld();

    ServerPlayer getPlayer();

    <H extends HitResult> H getHitResult();

    T getTarget();

    /**
     * Returns additional context synced from the client.
     *
     * @see IBlockComponentProvider#appendDataContext(IDataWriter, IBlockAccessor, IPluginConfig)
     * @see IEntityComponentProvider#appendDataContext(IDataWriter, IEntityAccessor, IPluginConfig)
     */
    IDataReader getContext();

}
