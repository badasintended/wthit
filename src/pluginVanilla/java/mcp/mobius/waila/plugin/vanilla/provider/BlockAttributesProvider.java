package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public enum BlockAttributesProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.ATTRIBUTE_BLOCK_POSITION)) {
            BlockPos pos = accessor.getPosition();
            tooltip.addLine(new TextComponent("(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")"));
        }

        if (config.getBoolean(Options.ATTRIBUTE_BLOCK_STATE)) {
            BlockState state = accessor.getBlockState();
            state.getProperties().forEach(property -> {
                Comparable<?> value = state.getValue(property);
                MutableComponent valueText = new TextComponent(value.toString());
                if (property instanceof BooleanProperty) {
                    valueText.withStyle(value == Boolean.TRUE ? ChatFormatting.GREEN : ChatFormatting.RED);
                }
                tooltip.addLine(new PairComponent(new TextComponent(property.getName()), valueText));
            });
        }
    }

}
