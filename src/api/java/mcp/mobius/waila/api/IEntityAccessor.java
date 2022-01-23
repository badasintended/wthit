package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * The Accessor is used to get some basic data out of the game without having to request direct access to the game engine.
 * <p>
 * It will also return things that are unmodified by the overriding systems (like getStack).
 */
@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IEntityAccessor {

    Level getWorld();

    Player getPlayer();

    <T extends Entity> T getEntity();

    HitResult getHitResult();

    @Nullable
    Vec3 getRenderingPosition();

    CompoundTag getServerData();

    double getPartialFrame();

    @Deprecated
    String getModNameFormat();

    @Deprecated
    String getEntityNameFormat();

    @Deprecated
    String getRegistryNameFormat();

}
