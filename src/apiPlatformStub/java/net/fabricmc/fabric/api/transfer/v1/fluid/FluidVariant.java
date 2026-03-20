package net.fabricmc.fabric.api.transfer.v1.fluid;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;

public interface FluidVariant {

    Fluid getFluid();

    DataComponentPatch getComponentsPatch();

}
