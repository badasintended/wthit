package mcp.mobius.waila.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jspecify.annotations.Nullable;

/**
 * Stub interface for Paper. On Fabric/Forge, this is a mixin accessor.
 */
public interface BeaconBlockEntityAccess {

    @Nullable
    Holder<MobEffect> wthit_primaryPower();

    @Nullable
    Holder<MobEffect> wthit_secondaryPower();

    int wthit_levels();

}
