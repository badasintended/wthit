package mcp.mobius.waila.api;

import mcp.mobius.waila.api.internal.ApiSide;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * The Accessor is used to get some basic data out of the game without having to request direct access to the game engine.<br>
 * It will also return things that are unmodified by the overriding systems (like getStack).<br>
 * An instance of this interface is passed to most of Waila Entity callbacks.
 */
@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IEntityAccessor {

    World getWorld();

    PlayerEntity getPlayer();

    Entity getEntity();

    HitResult getHitResult();

    @Nullable
    Vec3d getRenderingPosition();

    CompoundTag getServerData();

    double getPartialFrame();

    String getModNameFormat();

    String getEntityNameFormat();

    String getRegistryNameFormat();

}
