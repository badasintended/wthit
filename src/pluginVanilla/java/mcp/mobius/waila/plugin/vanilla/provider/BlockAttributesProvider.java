package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.PositionComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public enum BlockAttributesProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.BLOCK_POSITION)) {
            tooltip.setLine(Options.BLOCK_POSITION, new PositionComponent(accessor.getPosition()));
        }

        if (config.getBoolean(Options.BLOCK_STATE)) {
            var state = accessor.getBlockState();
            for (var property : state.getProperties()) {
                var value = state.getValue(property);
                var valueText = Component.literal(value.toString());
                if (property instanceof BooleanProperty) {
                    valueText.withStyle(value == Boolean.TRUE ? ChatFormatting.GREEN : ChatFormatting.RED);
                }
                var name = property.getName();
                tooltip.setLine(Options.BLOCK_STATE.withSuffix("." + name), new PairComponent(Component.literal(name), valueText));
            }
        }
    }

}
