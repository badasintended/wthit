package mcp.mobius.waila.util;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.service.ICommonService;

public final class ModInfo implements IModInfo {

    private static final Map<String, ModInfo> CONTAINER_CACHE = new HashMap<>();

    static {
        register(new ModInfo("minecraft", "Minecraft"));
    }

    private final String id;
    private final String name;

    public ModInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static void register(ModInfo info) {
        CONTAINER_CACHE.put(info.getId(), info);
    }

    public static ModInfo get(String namespace) {
        return CONTAINER_CACHE.computeIfAbsent(namespace, s -> ICommonService.INSTANCE.createModInfo(namespace).orElse(new ModInfo(s, s)));
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
