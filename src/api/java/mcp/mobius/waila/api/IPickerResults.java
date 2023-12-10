package mcp.mobius.waila.api;

import java.util.Iterator;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IPickerResults extends Iterable<HitResult> {

    /**
     * @param hitResult the hit result of the object
     * @param distance  the distance of the object from the cast origin
     */
    void add(HitResult hitResult, double distance);

    /**
     * @return the hit results, sorted by the distance from the cast origin
     */
    @Override
    Iterator<HitResult> iterator();

    /**
     * @return the distance of the hit result
     */
    double getDistance(HitResult hitResult);

}
