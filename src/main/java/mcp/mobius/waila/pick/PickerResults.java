package mcp.mobius.waila.pick;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import mcp.mobius.waila.api.IPickerResults;
import net.minecraft.world.phys.HitResult;

public enum PickerResults implements IPickerResults {

    INSTANCE;

    private final Object2DoubleMap<HitResult> map = new Object2DoubleArrayMap<>();

    @Override
    public void add(HitResult hitResult, double distance) {
        if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            map.put(hitResult, distance);
        }
    }

    @Override
    public Iterator<HitResult> iterator() {
        return map.object2DoubleEntrySet()
            .stream()
            .sorted(Comparator.comparingDouble(Object2DoubleMap.Entry::getDoubleValue))
            .map(Map.Entry::getKey)
            .iterator();
    }

    @Override
    public double getDistance(HitResult hitResult) {
        return map.getDouble(hitResult);
    }

    public static PickerResults get() {
        INSTANCE.map.clear();
        return INSTANCE;
    }

}
