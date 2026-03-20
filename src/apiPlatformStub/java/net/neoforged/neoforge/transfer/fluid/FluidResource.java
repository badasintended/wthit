package net.neoforged.neoforge.transfer.fluid;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;

public interface FluidResource {

    Fluid getFluid();

    DataComponentPatch getComponentsPatch();

}
