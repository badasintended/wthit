package mcp.mobius.waila.jaded.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import snownee.jade.impl.WailaCommonRegistration;

@Mixin(WailaCommonRegistration.class)
public interface AccessWailaCommonRegistration {

    @Invoker("<init>")
    static WailaCommonRegistration wthit_new() {
        throw new AssertionError("");
    }

    @Mutable
    @Accessor("INSTANCE")
    static void wthit_setInstance(WailaCommonRegistration instance) {
    }

}
