package mcp.mobius.waila.api;

import java.util.Iterator;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IPickerResults extends Iterable<HitResult> {

    /**
     * @param result     the hit result of the object
     * @param origin     the cast origin
     * @param viewVector the origin view vector
     */
    void add(HitResult result, Vec3 origin, Vec3 viewVector);

    /**
     * @return the hit results, sorted by the distance from the cast origin
     */
    @Override
    Iterator<HitResult> iterator();

    /**
     * @return the distance of the hit result from the cast origin
     */
    double getDistance(HitResult hitResult);

    /**
     * @deprecated use {@link #add(HitResult, Vec3, Vec3)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.21")
    void add(HitResult hitResult, double distance);

}
