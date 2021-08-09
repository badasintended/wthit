package mcp.mobius.waila.plugin.core;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public enum BlockComponent implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return;

        Block block = accessor.getBlock();
        String name = accessor.getBlockEntity() != null ? accessor.getServerData().getString("customName") : "";
        if (name.isEmpty()) {
            name = block.getName().getString();
        }

        IWailaConfig.Formatting formatting = IWailaConfig.get().getFormatting();
        tooltip.set(WailaConstants.OBJECT_NAME_TAG, new TextComponent(String.format(formatting.getBlockName(), name)));
        if (config.get(WailaConstants.CONFIG_SHOW_REGISTRY))
            tooltip.set(WailaConstants.REGISTRY_NAME_TAG, new TextComponent(String.format(formatting.getRegistryName(), Registry.BLOCK.getKey(block))));
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.get(WailaCore.CONFIG_SHOW_POS)) {
            BlockPos pos = accessor.getPosition();
            tooltip.add(new TextComponent("(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")"));
        }

        if (config.get(WailaCore.CONFIG_SHOW_STATES)) {
            BlockState state = accessor.getBlockState();
            state.getProperties().forEach(p -> {
                Comparable<?> value = state.getValue(p);
                Component valueText = new TextComponent(value.toString()).setStyle(Style.EMPTY.withColor(p instanceof BooleanProperty ? value == Boolean.TRUE ? ChatFormatting.GREEN : ChatFormatting.RED : ChatFormatting.RESET));
                tooltip.add(new TextComponent(p.getName() + ":").append(valueText));
            });
        }
    }

    @Override
    public void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.get(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            String modName = String.format(IWailaConfig.get().getFormatting().getModName(), IModInfo.get(accessor.getBlock()).getName());
            tooltip.set(WailaConstants.MOD_NAME_TAG, new TextComponent(modName));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity blockEntity) {
        if (blockEntity instanceof Nameable) {
            Component name = ((Nameable) blockEntity).getCustomName();
            if (name != null) {
                data.putString("customName", name.getString());
            }
        }
    }

}
