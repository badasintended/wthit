package mcp.mobius.waila.plugin.core.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.plugin.core.config.Options;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public enum BlockProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        return new ItemComponent(accessor.getBlock().getCloneItemStack(accessor.getWorld(), accessor.getPosition(), accessor.getBlockState()));
    }

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return;

        Block block = accessor.getBlock();
        String name = accessor.getBlockEntity() != null ? accessor.getServerData().getString("customName") : "";
        if (name.isEmpty()) {
            name = block.getName().getString();
        }

        IWailaConfig.Formatter formatter = IWailaConfig.get().getFormatter();
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.blockName(name));
        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY))
            tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, formatter.registryName(Registry.BLOCK.getKey(block)));
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.POS)) {
            BlockPos pos = accessor.getPosition();
            tooltip.addLine(new TextComponent("(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")"));
        }

        if (config.getBoolean(Options.STATES)) {
            BlockState state = accessor.getBlockState();
            state.getProperties().forEach(p -> {
                Comparable<?> value = state.getValue(p);
                Component valueText = new TextComponent(value.toString()).setStyle(Style.EMPTY.withColor(p instanceof BooleanProperty ? value == Boolean.TRUE ? ChatFormatting.GREEN : ChatFormatting.RED : ChatFormatting.RESET));
                tooltip.addLine(new PairComponent(new TextComponent(p.getName()), valueText));
            });
        }
    }

    @Override
    public void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            tooltip.setLine(WailaConstants.MOD_NAME_TAG, IWailaConfig.get().getFormatter().modName(IModInfo.get(accessor.getBlock()).getName()));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        if (accessor.getTarget() instanceof Nameable nameable) {
            Component name = nameable.getCustomName();
            if (name != null) {
                data.putString("customName", name.getString());
            }
        }
    }

}
