package mcp.mobius.waila.util;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.service.ICommonService;
import org.apache.commons.lang3.text.WordUtils;

public final class ModInfo implements IModInfo {

    private static final Map<String, ModInfo> CONTAINER_CACHE = new HashMap<>();

    private final boolean present;
    private final String id;
    private final String name;
    private final String version;

    public ModInfo(boolean present, String id, String name, String version) {
        this.present = present;
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public static void register(ModInfo info) {
        CONTAINER_CACHE.put(info.getId(), info);
    }

    public static ModInfo get(String namespace) {
        return CONTAINER_CACHE.computeIfAbsent(namespace, s ->
            ICommonService.INSTANCE.createModInfo(namespace)
            .or(() -> ICommonService.INSTANCE.createModInfo(namespace.replace('_', '-')))
            .orElse(new ModInfo(false, s, WordUtils.capitalizeFully(namespace.replace("_", " ")), "unknown"))
        );
    }

    @Override
    public boolean isPresent() {
        return present;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

}
