package net.neoforged.neoforge.fluids;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public interface FluidStack {

    Fluid getFluid();

    @Nullable
    CompoundTag getTag();

    int getAmount();

}
