package mcp.mobius.waila.api;

import java.util.Iterator;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated use {@link IRayCastVectorProvider}
 */
// TODO: Remove
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.21")
@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IPickerResults extends Iterable<HitResult> {

    void add(HitResult hitResult, double distance);

    @Override
    Iterator<HitResult> iterator();

    double getDistance(HitResult hitResult);

}
