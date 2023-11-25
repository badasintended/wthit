package mcp.mobius.waila.pick;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import mcp.mobius.waila.api.IPickerResults;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public enum PickerResults implements IPickerResults {

    INSTANCE;

    private final Map<HitResult, Entry> map = new IdentityHashMap<>();

    @Override
    public void add(HitResult result, Vec3 origin, Vec3 viewVector) {
        if (result == null || result.getType() == HitResult.Type.MISS) return;
        map.put(result, new Entry(origin, viewVector, origin.distanceToSqr(result.getLocation())));
    }

    @Override
    public void add(HitResult hitResult, double distance) {
        if (hitResult == null || hitResult.getType() == HitResult.Type.MISS) return;
        var camera = PickerAccessor.INSTANCE.getCameraEntity();
        var origin = camera == null ? Vec3.ZERO : camera.getEyePosition();
        var viewVec = camera == null ? Vec3.ZERO : camera.getViewVector(0);

        map.put(hitResult, new Entry(origin, viewVec, distance));
    }

    @Override
    public Iterator<HitResult> iterator() {
        return map.entrySet()
            .stream()
            .sorted(Comparator.comparingDouble(it -> it.getValue().distance))
            .map(Map.Entry::getKey)
            .iterator();
    }

    @Override
    public double getDistance(HitResult hitResult) {
        return map.get(hitResult).distance;
    }

    public Vec3 getOrigin(HitResult hitResult) {
        return map.get(hitResult).origin;
    }

    public Vec3 getViewVector(HitResult hitResult) {
        return map.get(hitResult).viewVec;
    }

    public static PickerResults get() {
        INSTANCE.map.clear();
        return INSTANCE;
    }

    private record Entry(Vec3 origin, Vec3 viewVec, double distance) {

    }

}
