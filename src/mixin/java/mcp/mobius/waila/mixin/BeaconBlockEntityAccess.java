package mcp.mobius.waila.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccess {

    @Nullable
    @Accessor("primaryPower")
    Holder<MobEffect> wthit_primaryPower();

    @Nullable
    @Accessor("secondaryPower")
    Holder<MobEffect> wthit_secondaryPower();

    @Accessor("levels")
    int wthit_levels();

}
