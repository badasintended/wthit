package mcp.mobius.waila.mixin;

import net.minecraft.world.LockCode;

/**
 * Stub interface for Paper. On Fabric/Forge, this is a mixin accessor
 * generated at runtime. On Paper, the cast to this interface will fail
 * with ClassCastException (caught by WTHIT's error handler).
 */
public interface BaseContainerBlockEntityAccess {

    LockCode wthit_lockKey();

    void wthit_lockKey(LockCode lockKey);

}
