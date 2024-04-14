package mcp.mobius.waila.api.fabric;

import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.api.data.FluidData.PlatformDependant;
import mcp.mobius.waila.api.data.FluidData.PlatformTranslator;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;

/**
 * Fabric-specific helper for creating {@link FluidData}.
 */
public final class FabricFluidData {

    public static final PlatformTranslator<FluidVariant> TRANSLATOR;

    /**
     * Creates a fluid data that accepts a {@link FluidVariant}
     */
    public static PlatformDependant<FluidVariant> of() {
        return FluidData.of(TRANSLATOR);
    }

    /**
     * Creates a fluid data that accepts a {@link FluidVariant}
     *
     * @param slotCountHint hint of how many the slots probably are to minimize growing the list more
     *                      than necessary, the user can call {@link FluidData#add} more than the specified count
     */
    public static PlatformDependant<FluidVariant> of(int slotCountHint) {
        return FluidData.of(TRANSLATOR, slotCountHint);
    }

    static {
        TRANSLATOR = new PlatformTranslator<>() {
            @Override
            public FluidData.Unit unit() {
                return FluidData.Unit.DROPLETS;
            }

            @Override
            public Fluid fluid(FluidVariant t) {
                return t.getFluid();
            }

            @Override
            public DataComponentPatch data(FluidVariant t) {
                return t.getComponents();
            }

            @Override
            public double amount(FluidVariant t) {
                throw new UnsupportedOperationException();
            }
        };
    }

}
