package mcp.mobius.waila.plugin.vanilla.component;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import static mcp.mobius.waila.plugin.vanilla.renderer.Renderers.ITEM;
import static mcp.mobius.waila.plugin.vanilla.renderer.Renderers.PROGRESS;

public enum FurnaceComponent implements IBlockComponentProvider, IServerDataProvider<AbstractFurnaceBlockEntity> {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.FURNACE_CONTENTS) && accessor.getServerData().contains("progress")) {
            ListTag furnaceItems = accessor.getServerData().getList("furnace", Tag.TAG_COMPOUND);

            tooltip.addDrawable()
                .with(ITEM, furnaceItems.getCompound(0))
                .with(ITEM, furnaceItems.getCompound(1))
                .with(PROGRESS, accessor.getServerData())
                .with(ITEM, furnaceItems.getCompound(2));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, AbstractFurnaceBlockEntity furnace) {
        if (furnace.getBlockState().getValue(FurnaceBlock.LIT)) {
            ListTag items = new ListTag();
            items.add(furnace.getItem(0).save(new CompoundTag()));
            items.add(furnace.getItem(1).save(new CompoundTag()));
            items.add(furnace.getItem(2).save(new CompoundTag()));
            data.put("furnace", items);

            data.putInt("progress", furnace.cookingProgress);
            data.putInt("total", furnace.cookingTotalTime);
        }
    }

}
