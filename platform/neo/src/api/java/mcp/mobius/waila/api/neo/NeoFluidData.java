package mcp.mobius.waila.api.neo;

import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.api.data.FluidData.PlatformDependant;
import mcp.mobius.waila.api.data.FluidData.PlatformTranslator;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

/**
 * NeoForge-specific helper for creating {@link FluidData}.
 */
public class NeoFluidData {

    public static final PlatformTranslator<FluidResource> TRANSLATOR;

    /**
     * Creates a fluid data that accepts a {@link FluidStack}
     */
    public static PlatformDependant<FluidResource> of() {
        return FluidData.of(TRANSLATOR);
    }

    /**
     * Creates a fluid data that accepts a {@link FluidResource}
     *
     * @param slotCountHint hint of how many the slots probably are to minimize growing the list more
     *                      than necessary, the user can call {@link FluidData#add} more than the specified count
     */
    public static PlatformDependant<FluidResource> of(int slotCountHint) {
        return FluidData.of(TRANSLATOR, slotCountHint);
    }

    static {
        TRANSLATOR = new PlatformTranslator<>() {
            @Override
            public FluidData.Unit unit() {
                return FluidData.Unit.MILLIBUCKETS;
            }

            @Override
            public Fluid fluid(FluidResource t) {
                return t.getFluid();
            }

            @Override
            public DataComponentPatch data(FluidResource t) {
                return t.getComponentsPatch();
            }

            @Override
            public double amount(FluidResource t) {
                throw new UnsupportedOperationException();
            }
        };
    }

}
