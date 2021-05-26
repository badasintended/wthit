package mcp.mobius.waila.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * The Accessor is used to get some basic data out of the game without having to request direct access to the game engine.<br>
 * It will also return things that are unmodified by the overriding systems (like getStack).<br>
 * An instance of this interface is passed to most of Waila Entity callbacks.
 */
public interface IEntityAccessor {

    World getWorld();

    PlayerEntity getPlayer();

    Entity getEntity();

    HitResult getHitResult();

    @Nullable
    Vec3d getRenderingPosition();

    NbtCompound getServerData();

    double getPartialFrame();

    String getModNameFormat();

    String getEntityNameFormat();

    String getRegistryNameFormat();

}
