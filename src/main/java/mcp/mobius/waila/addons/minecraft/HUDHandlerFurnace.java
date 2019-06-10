package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.*;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class HUDHandlerFurnace implements IComponentProvider, IServerDataProvider<BlockEntity> {

    static final HUDHandlerFurnace INSTANCE = new HUDHandlerFurnace();

    @Override
    public void appendBody(List<Component> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (!config.get(PluginMinecraft.CONFIG_DISPLAY_FURNACE))
            return;

        if (!accessor.getBlockState().get(Properties.LIT))
            return;

        ListTag furnaceItems = accessor.getServerData().getList("furnace", NbtType.COMPOUND);
        DefaultedList<ItemStack> inventory = DefaultedList.create(3, ItemStack.EMPTY);
        for (int i = 0; i <furnaceItems.size(); i++)
            inventory.set(i, ItemStack.fromTag(furnaceItems.getCompoundTag(i)));

        CompoundTag progress = new CompoundTag();
        progress.putInt("progress", accessor.getServerData().getInt("progress"));
        progress.putInt("total", accessor.getServerData().getInt("total"));

        RenderableTextComponent renderables = new RenderableTextComponent(
                getRenderable(inventory.get(0)),
                getRenderable(inventory.get(1)),
                new RenderableTextComponent(PluginMinecraft.RENDER_FURNACE_PROGRESS, progress),
                getRenderable(inventory.get(2))
        );

        tooltip.add(renderables);
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) blockEntity;
        ListTag items = new ListTag();
        items.add(furnace.getInvStack(0).toTag(new CompoundTag()));
        items.add(furnace.getInvStack(1).toTag(new CompoundTag()));
        items.add(furnace.getInvStack(2).toTag(new CompoundTag()));
        data.put("furnace", items);
        CompoundTag furnaceTag = furnace.toTag(new CompoundTag());
        data.putInt("progress", furnaceTag.getInt("CookTime")); // smh
        data.putInt("total", furnaceTag.getInt("CookTimeTotal")); // smh
    }

    private static RenderableTextComponent getRenderable(ItemStack stack) {
        if (!stack.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", Registry.ITEM.getId(stack.getItem()).toString());
            tag.putInt("count", stack.getCount());
            if (stack.hasTag())
                tag.putString("nbt", stack.getTag().toString());
            return new RenderableTextComponent(PluginMinecraft.RENDER_ITEM, tag);
        } else {
            CompoundTag spacerTag = new CompoundTag();
            spacerTag.putInt("width", 18);
            return new RenderableTextComponent(PluginMinecraft.RENDER_SPACER, spacerTag);
        }
    }
}
