package mcp.mobius.waila.mixin;

import net.minecraft.world.LockCode;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BaseContainerBlockEntity.class)
public interface BaseContainerBlockEntityAccess {

    @Accessor("lockKey")
    LockCode wthit_lockKey();

    @Accessor("lockKey")
    void wthit_lockKey(LockCode lockKey);

}
