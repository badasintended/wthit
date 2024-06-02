package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.EnergyData;
import mcp.mobius.waila.api.data.FluidData;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.material.Fluids;

public enum ExtraTest implements IDataProvider<ChestBlockEntity> {

    INSTANCE;

    public static final ResourceLocation ENERGY = ResourceLocation.parse("test:energy");
    public static final ResourceLocation ENERGY_INF_STORED = ResourceLocation.parse("test:energy.inf_stored");
    public static final ResourceLocation ENERGY_INF_CAPACITY = ResourceLocation.parse("test:energy.inf_capacity");

    public static final ResourceLocation FLUID = ResourceLocation.parse("test:fluid");

    private int tickEnergy = 0;
    private int tickWater = 0;
    private int tickLava = 0;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<ChestBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(ENERGY)) data.add(EnergyData.TYPE, res -> {
            tickEnergy++;
            if (tickEnergy == 500) tickEnergy = 0;

            if (config.getBoolean(ENERGY_INF_STORED)) res.add(EnergyData.of(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
            else if (config.getBoolean(ENERGY_INF_CAPACITY)) res.add(EnergyData.of(tickEnergy * 100L, Double.POSITIVE_INFINITY));
            else res.add(EnergyData.of(tickEnergy * 100L, 50000L));
        });

        if (config.getBoolean(FLUID)) data.add(FluidData.TYPE, res -> {
            tickWater++;
            if (tickWater == 500) tickWater = 0;

            tickLava++;
            if (tickLava == 250) tickLava = 0;

            res.add(FluidData.of(FluidData.Unit.MILLIBUCKETS, 2)
                .add(Fluids.WATER, DataComponentPatch.EMPTY, tickWater * 100.0, 50000.0)
                .add(Fluids.LAVA, DataComponentPatch.EMPTY, tickLava * 100.0, 25000.0));
        });
    }

}
