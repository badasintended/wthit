package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.EnergyData;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public enum ExtraTest implements IDataProvider<ChestBlockEntity> {

    INSTANCE;

    public static final ResourceLocation ENERGY = new ResourceLocation("test:energy");
    public static final ResourceLocation ENERGY_INF_STORED = new ResourceLocation("test:energy.inf_stored");
    public static final ResourceLocation ENERGY_INF_CAPACITY = new ResourceLocation("test:energy.inf_capacity");

    private int tick = 0;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<ChestBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(ENERGY)) data.add(EnergyData.class, res -> {
            tick++;
            if (tick == 500) tick = 0;

            if (config.getBoolean(ENERGY_INF_STORED)) res.add(EnergyData.infinite());
            else if (config.getBoolean(ENERGY_INF_CAPACITY)) res.add(EnergyData.endlessCapacity().stored(tick * 100L));
            else res.add(EnergyData
                    .capacity(50000L)
                    .stored(tick * 100L)
                    .nameTraslationKey(Tl.Tooltip.POWER)
                    .color(0xFF00FF)
                    .unit("TEST"));
        });
    }

}
