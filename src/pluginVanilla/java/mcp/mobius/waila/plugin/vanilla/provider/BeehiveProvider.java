package mcp.mobius.waila.plugin.vanilla.provider;

import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.BeehiveDataProvider;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.BeehiveBlock;

public enum BeehiveProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        var occupants = accessor.getData().get(BeehiveDataProvider.OCCUPANTS);
        if (occupants != null && config.getBoolean(Options.BEE_HIVE_OCCUPANTS)) {
            var names = new Object2IntLinkedOpenHashMap<String>(occupants.occupants().size());

            for (var occupant : occupants.occupants()) {
                Component component = null;
                if (occupant.customName() != null) {
                    component = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, GsonHelper.parse(occupant.customName())).result().orElse(null);
                }
                if (component == null) component = occupant.entityType().getDescription();

                var name = component.getString();
                names.put(name, names.getOrDefault(name, 0) + 1);
            }

            if (!names.isEmpty()) {
                var component = Component.empty();

                for (var entry : names.object2IntEntrySet()) {
                    if (!component.getSiblings().isEmpty()) component.append(CommonComponents.NEW_LINE);
                    var name = entry.getKey();
                    var count = entry.getIntValue();
                    if (count > 1) component.append(Component.literal(count + " " + name));
                    else component.append(Component.literal(name));
                }

                tooltip.setLine(Options.BEE_HIVE_OCCUPANTS, component);
            }
        }

        if (config.getBoolean(Options.BEE_HIVE_HONEY_LEVEL)) {
            var state = accessor.getBlockState();
            tooltip.setLine(Options.BEE_HIVE_HONEY_LEVEL, new PairComponent(
                Component.translatable(Tl.Tooltip.HONEY_LEVEL),
                Component.literal(state.getValue(BeehiveBlock.HONEY_LEVEL).toString())));
        }
    }

}
