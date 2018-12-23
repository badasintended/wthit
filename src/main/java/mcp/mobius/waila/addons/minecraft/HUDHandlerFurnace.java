package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.*;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class HUDHandlerFurnace implements IWailaDataProvider, IServerDataProvider<BlockEntity> {

    static final HUDHandlerFurnace INSTANCE = new HUDHandlerFurnace();

    @Override
    public void appendBody(List<TextComponent> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (!config.get(PluginMinecraft.CONFIG_DISPLAY_FURNACE))
            return;

        if (!accessor.getBlockState().get(Properties.LIT))
            return;

        CompoundTag furnaceData = accessor.getServerData().getCompound("furnace");
        int cookTime = furnaceData.getShort("CookTime");

        DefaultedList<ItemStack> inventory = DefaultedList.create();
        InventoryUtil.deserialize(furnaceData, inventory);

        CompoundTag progress = new CompoundTag();
        progress.putInt("cook", cookTime);

        RenderableTextComponent renderables = new RenderableTextComponent(
                getRenderable(inventory.get(0)),
                getRenderable(inventory.get(1)),
                getRenderable(inventory.get(2)),
                new RenderableTextComponent(PluginMinecraft.RENDER_FURNACE_PROGRESS, progress)
        );

        tooltip.add(renderables);
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) blockEntity;
        data.put("furnace", furnace.toTag(new CompoundTag()));
    }

    private static RenderableTextComponent getRenderable(ItemStack stack) {
        if (stack.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", Registry.ITEM.getId(stack.getItem()).toString());
            tag.putInt("count", stack.getAmount());
            if (stack.hasTag())
                tag.putString("nbt", stack.getTag().toString());
            return new RenderableTextComponent(PluginMinecraft.RENDER_ITEM, tag);
        } else return new RenderableTextComponent(PluginMinecraft.RENDER_SPACER, new CompoundTag());
    }
}
