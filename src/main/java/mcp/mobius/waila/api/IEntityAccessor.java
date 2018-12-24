package mcp.mobius.waila.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * The Accessor is used to get some basic data out of the game without having to request direct access to the game engine.<br>
 * It will also return things that are unmodified by the overriding systems (like getStack).<br>
 * An instance of this interface is passed to most of Waila Entity callbacks.
 *
 * @author ProfMobius
 */

public interface IEntityAccessor {

    World getWorld();

    PlayerEntity getPlayer();

    LivingEntity getEntity();

    HitResult getHitResult();

    Vec3d getRenderingPosition();

    CompoundTag getServerData();

    double getPartialFrame();
}
