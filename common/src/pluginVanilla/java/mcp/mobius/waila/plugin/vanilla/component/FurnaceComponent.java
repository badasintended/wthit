package mcp.mobius.waila.plugin.vanilla.component;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static mcp.mobius.waila.plugin.vanilla.renderer.Renderers.ITEM;
import static mcp.mobius.waila.plugin.vanilla.renderer.Renderers.PROGRESS;

public enum FurnaceComponent implements IBlockComponentProvider, IServerDataProvider<AbstractFurnaceBlockEntity> {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.FURNACE_CONTENTS))
            return;

        if (!accessor.getBlockState().getValue(BlockStateProperties.LIT))
            return;

        ListTag furnaceItems = accessor.getServerData().getList("furnace", Tag.TAG_COMPOUND);

        tooltip.add(IDrawableText.create()
            .with(ITEM, furnaceItems.getCompound(0))
            .with(ITEM, furnaceItems.getCompound(1))
            .with(PROGRESS, accessor.getServerData())
            .with(ITEM, furnaceItems.getCompound(2)));
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, AbstractFurnaceBlockEntity furnaceBlockEntity) {
        ListTag items = new ListTag();
        items.add(furnaceBlockEntity.getItem(0).save(new CompoundTag()));
        items.add(furnaceBlockEntity.getItem(1).save(new CompoundTag()));
        items.add(furnaceBlockEntity.getItem(2).save(new CompoundTag()));
        data.put("furnace", items);

        data.putInt("progress", furnaceBlockEntity.cookingProgress);
        data.putInt("total", furnaceBlockEntity.cookingTotalTime);
    }

}
