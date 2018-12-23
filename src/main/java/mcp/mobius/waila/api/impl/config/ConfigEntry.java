package mcp.mobius.waila.api.impl.config;

import net.minecraft.util.Identifier;

public class ConfigEntry {

    private final Identifier id;
    private final boolean defaultValue;
    private final boolean synced;
    private boolean value;

    public ConfigEntry(Identifier id, boolean defaultValue, boolean synced) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.synced = synced;
    }

    public Identifier getId() {
        return id;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    public boolean isSynced() {
        return synced;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
