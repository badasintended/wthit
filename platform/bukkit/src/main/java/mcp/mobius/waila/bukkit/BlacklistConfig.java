package mcp.mobius.waila.bukkit;

import java.util.LinkedHashSet;

import org.bukkit.NamespacedKey;

@SuppressWarnings("unused")
public class BlacklistConfig {

    public final LinkedHashSet<NamespacedKey> blocks = new LinkedHashSet<>();
    public final LinkedHashSet<NamespacedKey> blockEntityTypes = new LinkedHashSet<>();
    public final LinkedHashSet<NamespacedKey> entityTypes = new LinkedHashSet<>();

    public final int configVersion = 0;
    public final int[] pluginHash = {0, 0, 0};

}
