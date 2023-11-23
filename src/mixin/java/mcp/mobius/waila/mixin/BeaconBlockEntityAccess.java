package mcp.mobius.waila.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccess {

    @Nullable
    @Accessor("primaryPower")
    MobEffect wthit_primaryPower();

    @Nullable
    @Accessor("secondaryPower")
    MobEffect wthit_secondaryPower();

}
