package mcp.mobius.waila.plugin.core.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public enum FluidProvider implements IBlockComponentProvider {

    INSTANCE;

    @Nullable
    @Override
    public ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() == Blocks.WATER)
            return new ItemComponent(new ItemStack(Items.WATER_BUCKET));
        else if (accessor.getBlock() == Blocks.LAVA)
            return new ItemComponent(new ItemStack(Items.LAVA_BUCKET));
        return null;
    }

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        Block block = accessor.getBlock();
        IWailaConfig.Formatting formatting = IWailaConfig.get().getFormatting();
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, new TextComponent(formatting.formatFluidName(block.getName().getString())));
        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
            tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, new TextComponent(formatting.formatRegistryName(Registry.BLOCK.getKey(block))));
        }
    }

}
