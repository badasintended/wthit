package mcp.mobius.waila.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class InventoryUtils {

    public static NBTTagList invToNBT(IItemHandler itemHandler) {
        NBTTagList nbtTagList = new NBTTagList();
        int size = itemHandler.getSlots();
        for (int i = 0; i < size; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setInteger("Slot", i);
                writeStack(stack, itemTag);
                nbtTagList.appendTag(itemTag);
            }
        }

        return nbtTagList;
    }

    public static void populateInv(IItemHandlerModifiable itemHandler, NBTTagList tagList) {
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");

            if (slot >= 0 && slot < itemHandler.getSlots())
                itemHandler.setStackInSlot(slot, readStack(itemTags));
        }
    }

    public static void writeStack(ItemStack stack, NBTTagCompound tagCompound) {
        stack.writeToNBT(tagCompound);
        tagCompound.setInteger("CountI", stack.getCount());
    }

    public static ItemStack readStack(NBTTagCompound tagCompound) {
        ItemStack stack = new ItemStack(tagCompound);
        stack.setCount(tagCompound.getInteger("CountI"));
        return stack;
    }

    public static void addStack(List<ItemStack> stacks, ItemStack stack) {
        for (ItemStack invStack : stacks) {
            if (ItemHandlerHelper.canItemStacksStack(invStack, stack)) {
                invStack.grow(stack.getCount());
                return;
            }
        }

        stacks.add(stack.copy());
    }
}
