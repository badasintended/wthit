package mcp.mobius.waila.plugin.vanilla.provider;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.BeehiveDataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.BeehiveBlock;

public enum BeehiveProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        var occupants = accessor.getData().get(BeehiveDataProvider.OccupantsData.class);
        if (occupants != null && config.getBoolean(Options.BEE_HIVE_OCCUPANTS)) {
            var names = new Object2IntLinkedOpenHashMap<String>(occupants.occupants().size());

            for (var occupant : occupants.occupants()) {
                Component component = null;
                if (occupant.customName() != null) component = Component.Serializer.fromJson(occupant.customName());
                if (component == null) component = occupant.entityType().getDescription();

                var name = component.getString();
                names.put(name, names.getOrDefault(name, 0) + 1);
            }

            for (var entry : names.object2IntEntrySet()) {
                var name = entry.getKey();
                var count = entry.getIntValue();
                if (count > 1) tooltip.addLine(Component.literal(count + " " + name));
                else tooltip.addLine(Component.literal(name));
            }
        }

        if (config.getBoolean(Options.BEE_HIVE_HONEY_LEVEL)) {
            var state = accessor.getBlockState();
            tooltip.addLine(new PairComponent(
                Component.translatable(Tl.Tooltip.HONEY_LEVEL),
                Component.literal(state.getValue(BeehiveBlock.HONEY_LEVEL).toString())));
        }
    }

}
