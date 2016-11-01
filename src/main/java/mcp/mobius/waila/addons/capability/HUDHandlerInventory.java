package mcp.mobius.waila.addons.capability;

import mcp.mobius.waila.addons.HUDHandlerBase;
import mcp.mobius.waila.api.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.*;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.ArrayList;
import java.util.List;

public class HUDHandlerInventory extends HUDHandlerBase {

    static final IWailaDataProvider INSTANCE = new HUDHandlerInventory();

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (!config.getConfig("capability.inventoryinfo") || accessor.getTileEntity() == null || accessor.getTileEntity().getClass() == TileEntityFurnace.class)
            return currenttip;

        if (accessor.getNBTData().hasKey("handler")) {
            int handlerSize = accessor.getNBTData().getInteger("handlerSize");
            IItemHandler itemHandler = new ItemStackHandler(handlerSize);
            populateInv((ItemStackHandler) itemHandler, accessor.getNBTData().getTagList("handler", 10));

            List<ItemStack> toRender = new ArrayList<ItemStack>();
            for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                ItemStack stack = itemHandler.getStackInSlot(slot);
                if (stack == null)
                    continue;

                addStack(toRender, stack);
            }

            String renderString = "";
            int drawnCount = 0;
            for (ItemStack stack : toRender) {
                String name = stack.getItem().getRegistryName().toString();
                if (drawnCount > 5 && !accessor.getPlayer().isSneaking())
                    break;
                else if (drawnCount > 5 && accessor.getPlayer().isSneaking()) {
                    currenttip.add(renderString);
                    renderString = "";
                    drawnCount = 0;
                }

                renderString += SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(stack.stackSize), String.valueOf(stack.getItemDamage()));
                drawnCount += 1;
            }

            ((ITaggedList<String, String>) currenttip).add(renderString, "IItemHandler");
        }
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te != null) {
            te.writeToNBT(tag);
            if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                tag.setTag("handler", invToNBT(itemHandler));
                tag.setInteger("handlerSize", itemHandler.getSlots());
            } else if (te instanceof IInventory) {
                IItemHandler itemHandler = new InvWrapper((IInventory) te);
                tag.setTag("handler", invToNBT(itemHandler));
                tag.setInteger("handlerSize", itemHandler.getSlots());
            } else if (te instanceof TileEntityEnderChest) {
                IItemHandler itemHandler = new InvWrapper(player.getInventoryEnderChest());
                tag.setTag("handler", invToNBT(itemHandler));
                tag.setInteger("handlerSize", itemHandler.getSlots());
            }
        }

        return tag;
    }

    private NBTTagList invToNBT(IItemHandler itemHandler) {
        NBTTagList nbtTagList = new NBTTagList();
        int size = itemHandler.getSlots();
        for (int i = 0; i < size; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setInteger("Slot", i);
                writeStack(stack, itemTag);
                nbtTagList.appendTag(itemTag);
            }
        }

        return nbtTagList;
    }

    private void populateInv(IItemHandlerModifiable itemHandler, NBTTagList tagList) {
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");

            if (slot >= 0 && slot < itemHandler.getSlots())
                itemHandler.setStackInSlot(slot, readStack(itemTags));
        }
    }

    private void writeStack(ItemStack stack, NBTTagCompound tagCompound) {
        stack.writeToNBT(tagCompound);
        tagCompound.setInteger("CountI", stack.stackSize);
    }

    private ItemStack readStack(NBTTagCompound tagCompound) {
        ItemStack stack = ItemStack.loadItemStackFromNBT(tagCompound);
        stack.stackSize = tagCompound.getInteger("CountI");
        return stack;
    }

    private void addStack(List<ItemStack> stacks, ItemStack stack) {
        for (ItemStack invStack : stacks) {
            if (ItemHandlerHelper.canItemStacksStack(invStack, stack)) {
                invStack.stackSize += stack.stackSize;
                return;
            }
        }

        stacks.add(stack.copy());
    }
}
