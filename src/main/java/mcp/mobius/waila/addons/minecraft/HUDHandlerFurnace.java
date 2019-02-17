package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class HUDHandlerFurnace implements IComponentProvider, IServerDataProvider<TileEntity> {

    static final HUDHandlerFurnace INSTANCE = new HUDHandlerFurnace();

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (!config.get(PluginMinecraft.CONFIG_DISPLAY_FURNACE))
            return;

        if (!accessor.getBlockState().get(BlockStateProperties.LIT))
            return;

        NBTTagList furnaceItems = accessor.getServerData().getList("furnace", Constants.NBT.TAG_COMPOUND);
        NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
        for (int i = 0; i <furnaceItems.size(); i++)
            inventory.set(i, ItemStack.read(furnaceItems.getCompound(i)));

        NBTTagCompound progress = new NBTTagCompound();
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
    public void appendServerData(NBTTagCompound data, EntityPlayerMP player, World world, TileEntity blockEntity) {
        TileEntityFurnace furnace = (TileEntityFurnace) blockEntity;
        NBTTagList items = new NBTTagList();
        items.add(furnace.getStackInSlot(0).write(new NBTTagCompound()));
        items.add(furnace.getStackInSlot(1).write(new NBTTagCompound()));
        items.add(furnace.getStackInSlot(2).write(new NBTTagCompound()));
        data.put("furnace", items);
        NBTTagCompound furnaceTag = furnace.write(new NBTTagCompound());
        data.putInt("progress", furnaceTag.getInt("CookTime")); // smh
        data.putInt("total", furnaceTag.getInt("CookTimeTotal")); // smh
    }

    private static RenderableTextComponent getRenderable(ItemStack stack) {
        if (!stack.isEmpty()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.putString("id", stack.getItem().getRegistryName().toString());
            tag.putInt("count", stack.getCount());
            if (stack.hasTag())
                tag.putString("nbt", stack.getTag().toString());
            return new RenderableTextComponent(PluginMinecraft.RENDER_ITEM, tag);
        } else {
            NBTTagCompound spacerTag = new NBTTagCompound();
            spacerTag.putInt("width", 18);
            return new RenderableTextComponent(PluginMinecraft.RENDER_SPACER, spacerTag);
        }
    }
}
