package mcp.mobius.waila.jaded.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import snownee.jade.impl.config.PluginConfig;

@Mixin(PluginConfig.class)
public interface AccessPluginConfig {

    @Invoker("<init>")
    static PluginConfig wthit_new() {
        throw new AssertionError("");
    }

    @Mutable
    @Accessor("INSTANCE")
    static void wthit_setInstance(PluginConfig instance) {
    }

}
