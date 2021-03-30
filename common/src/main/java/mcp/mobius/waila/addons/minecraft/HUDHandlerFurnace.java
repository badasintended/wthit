package mcp.mobius.waila.addons.minecraft;

import java.util.List;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class HUDHandlerFurnace implements IComponentProvider, IServerDataProvider<BlockEntity> {

    static final HUDHandlerFurnace INSTANCE = new HUDHandlerFurnace();

    @Override
    public void appendBody(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (!config.get(PluginMinecraft.CONFIG_DISPLAY_FURNACE))
            return;

        if (!accessor.getBlockState().get(Properties.LIT))
            return;

        ListTag furnaceItems = accessor.getServerData().getList("furnace", 10 /* COMPOUND */);
        DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
        for (int i = 0; i < furnaceItems.size(); i++)
            inventory.set(i, ItemStack.fromNbt(furnaceItems.getCompound(i)));

        CompoundTag progress = new CompoundTag();
        progress.putInt("progress", accessor.getServerData().getInt("progress"));
        progress.putInt("total", accessor.getServerData().getInt("total"));

        IDrawableText drawables = IDrawableText.of(
            getDrawable(inventory.get(0)),
            getDrawable(inventory.get(1)),
            IDrawableText.of(PluginMinecraft.RENDER_FURNACE_PROGRESS, progress),
            getDrawable(inventory.get(2))
        );

        tooltip.add(drawables);
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) blockEntity;
        ListTag items = new ListTag();
        items.add(furnace.getStack(0).writeNbt(new CompoundTag()));
        items.add(furnace.getStack(1).writeNbt(new CompoundTag()));
        items.add(furnace.getStack(2).writeNbt(new CompoundTag()));
        data.put("furnace", items);
        CompoundTag furnaceTag = furnace.writeNbt(new CompoundTag());
        data.putInt("progress", furnaceTag.getInt("CookTime")); // smh
        data.putInt("total", furnaceTag.getInt("CookTimeTotal")); // smh
    }

    private static IDrawableText getDrawable(ItemStack stack) {
        if (!stack.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", Registry.ITEM.getId(stack.getItem()).toString());
            tag.putInt("count", stack.getCount());
            if (stack.hasTag())
                tag.putString("nbt", stack.getTag().toString());
            return IDrawableText.of(PluginMinecraft.RENDER_ITEM, tag);
        } else {
            CompoundTag spacerTag = new CompoundTag();
            spacerTag.putInt("width", 18);
            return IDrawableText.of(PluginMinecraft.RENDER_SPACER, spacerTag);
        }
    }

}
