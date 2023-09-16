package mcp.mobius.waila.plugin.extra.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaHelper;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.SpriteBarComponent;
import mcp.mobius.waila.api.component.WrappedComponent;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.plugin.extra.data.FluidDescription;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class FluidProvider extends DataProvider<FluidData> {

    public static final FluidProvider INSTANCE = new FluidProvider();

    private static final String INFINITE = "∞";

    private FluidProvider() {
        super(FluidData.ID, FluidData.class, FluidData::new);
    }

    @Override
    protected void registerAdditions(IRegistrar registrar, int priority) {
        registrar.addConfig(FluidData.CONFIG_DISPLAY_UNIT, FluidData.Unit.MILLIBUCKETS);
        registrar.addComponent(new CauldronProvider(), TooltipPosition.BODY, Block.class, priority);
    }

    @Override
    protected void appendBody(ITooltip tooltip, FluidData data, IPluginConfig config, ResourceLocation objectId) {
        addFluidTooltip(tooltip, data, config);
    }

    private void addFluidTooltip(ITooltip tooltip, FluidData data, IPluginConfig config) {
        FluidData.Unit displayUnit = config.getEnum(FluidData.CONFIG_DISPLAY_UNIT);
        var storedUnit = data.unit();

        for (var entry : data.entries()) {
            if (entry.isEmpty()) continue;

            var desc = FluidDescription.getFluidDesc(entry);

            var stored = entry.stored();
            var capacity = entry.capacity();
            var ratio = Double.isInfinite(capacity) ? 1f : (float) (stored / capacity);

            String text;
            if (Double.isInfinite(stored)) text = INFINITE;
            else {
                text = WailaHelper.suffix((long) FluidData.Unit.convert(storedUnit, displayUnit, stored));
                if (Double.isFinite(capacity)) text += "/" + WailaHelper.suffix((long) FluidData.Unit.convert(storedUnit, displayUnit, capacity));
            }

            text += " " + displayUnit.symbol;

            var sprite = desc.sprite();
            tooltip.addLine(new PairComponent(
                new WrappedComponent(desc.name().getString()),
                new SpriteBarComponent(ratio, sprite, 16, 16, desc.tint(), Component.literal(text))));
        }
    }

    private class CauldronProvider implements IBlockComponentProvider {

        @Override
        public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            if (accessor.getData().get(FluidData.class) != null) return;
            if (!config.getBoolean(enabledBlockOption)) return;
            if (blacklistConfig.get().getView().blockFilter.matches(accessor.getBlock())) return;

            var fluidData = FluidDescription.getCauldronFluidData(accessor.getBlockState());
            if (fluidData != null) addFluidTooltip(tooltip, fluidData, config);
        }

    }

}
