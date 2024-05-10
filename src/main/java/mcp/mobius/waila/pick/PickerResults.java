package mcp.mobius.waila.pick;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.function.ObjDoubleConsumer;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.world.phys.HitResult;

public enum PickerResults implements Iterable<HitResult>, ObjDoubleConsumer<HitResult> {

    INSTANCE;

    private final Object2DoubleMap<HitResult> map = new Object2DoubleArrayMap<>();

    @Override
    public void accept(HitResult hitResult, double value) {
        if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            map.put(hitResult, value);
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

    public static PickerResults get() {
        INSTANCE.map.clear();
        return INSTANCE;
    }

}
