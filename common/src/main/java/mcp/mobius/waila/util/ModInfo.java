package mcp.mobius.waila.util;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.Maps;
import mcp.mobius.waila.api.IModInfo;

public record ModInfo(String id, String name) implements IModInfo {

    public static Function<String, Optional<ModInfo>> supplier;

    private static final Map<String, ModInfo> CONTAINER_CACHE = Maps.newHashMap();

    static {
        register(new ModInfo("minecraft", "Minecraft"));
    }

    public static void register(ModInfo info) {
        CONTAINER_CACHE.put(info.getId(), info);
    }

    public static ModInfo get(String namespace) {
        return CONTAINER_CACHE.computeIfAbsent(namespace, s -> supplier.apply(namespace).orElse(new ModInfo(s, s)));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

}
