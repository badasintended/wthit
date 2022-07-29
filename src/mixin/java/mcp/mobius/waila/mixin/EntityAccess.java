package mcp.mobius.waila.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccess {

    @Invoker("getTypeName")
    Component wthit_getTypeName();

}
