package net.neoforged.neoforge.fluids;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;

public interface FluidStack {

    Fluid getFluid();

    DataComponentPatch getComponentsPatch();

    int getAmount();

}
