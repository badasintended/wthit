package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static mcp.mobius.waila.plugin.vanilla.WailaVanilla.RENDER_FURNACE_PROGRESS;
import static mcp.mobius.waila.plugin.vanilla.WailaVanilla.RENDER_ITEM;

public enum FurnaceComponent implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(WailaVanilla.CONFIG_DISPLAY_FURNACE))
            return;

        if (!accessor.getBlockState().getValue(BlockStateProperties.LIT))
            return;

        ListTag furnaceItems = accessor.getServerData().getList("furnace", Tag.TAG_COMPOUND);

        tooltip.add(IDrawableText.create()
            .with(RENDER_ITEM, furnaceItems.getCompound(0))
            .with(RENDER_ITEM, furnaceItems.getCompound(1))
            .with(RENDER_FURNACE_PROGRESS, accessor.getServerData())
            .with(RENDER_ITEM, furnaceItems.getCompound(2)));
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity blockEntity) {
        AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) blockEntity;
        ListTag items = new ListTag();
        items.add(furnace.getItem(0).save(new CompoundTag()));
        items.add(furnace.getItem(1).save(new CompoundTag()));
        items.add(furnace.getItem(2).save(new CompoundTag()));
        data.put("furnace", items);

        data.putInt("progress", furnace.cookingProgress);
        data.putInt("total", furnace.cookingTotalTime);
    }

}
