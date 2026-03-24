package net.fabricmc.fabric.api.transfer.v1.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public interface FluidVariant {

    Fluid getFluid();

    @Nullable
    CompoundTag getNbt();

}
