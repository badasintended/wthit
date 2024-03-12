package mcp.mobius.waila.jaded.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import snownee.jade.impl.WailaClientRegistration;

@Mixin(WailaClientRegistration.class)
public interface AccessWailaClientRegistration {

    @Invoker("<init>")
    static WailaClientRegistration wthit_new() {
        throw new AssertionError("");
    }

    @Mutable
    @Accessor("INSTANCE")
    static void wthit_setInstance(WailaClientRegistration instance) {
    }

}
