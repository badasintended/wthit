package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum ComposterComponent implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.get(WailaVanilla.CONFIG_COMPOSTER_LEVEL)) {
            BlockState state = accessor.getBlockState();
            tooltip.add(new TranslatableText("tooltip.waila.compost_level", state.get(ComposterBlock.LEVEL)));
        }
    }
}
