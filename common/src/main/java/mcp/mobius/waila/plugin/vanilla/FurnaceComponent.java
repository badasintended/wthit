package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import static mcp.mobius.waila.plugin.vanilla.WailaVanilla.RENDER_FURNACE_PROGRESS;
import static mcp.mobius.waila.plugin.vanilla.WailaVanilla.RENDER_ITEM;

public enum FurnaceComponent implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!config.get(WailaVanilla.CONFIG_DISPLAY_FURNACE))
            return;

        if (!accessor.getBlockState().get(Properties.LIT))
            return;

        ListTag furnaceItems = accessor.getServerData().getList("furnace", 10 /* COMPOUND */);

        tooltip.add(IDrawableText.create()
            .with(RENDER_ITEM, furnaceItems.getCompound(0))
            .with(RENDER_ITEM, furnaceItems.getCompound(1))
            .with(RENDER_FURNACE_PROGRESS, accessor.getServerData())
            .with(RENDER_ITEM, furnaceItems.getCompound(2)));
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) blockEntity;
        ListTag items = new ListTag();
        items.add(furnace.getStack(0).toTag(new CompoundTag()));
        items.add(furnace.getStack(1).toTag(new CompoundTag()));
        items.add(furnace.getStack(2).toTag(new CompoundTag()));
        data.put("furnace", items);
        CompoundTag furnaceTag = furnace.toTag(new CompoundTag());
        data.putInt("progress", furnaceTag.getInt("CookTime")); // smh
        data.putInt("total", furnaceTag.getInt("CookTimeTotal")); // smh
    }

}
