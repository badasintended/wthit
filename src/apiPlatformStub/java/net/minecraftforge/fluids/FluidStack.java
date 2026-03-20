package net.minecraftforge.fluids;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;

public interface FluidStack {

    Fluid getFluid();

    CompoundTag getTag();

    int getAmount();

}
