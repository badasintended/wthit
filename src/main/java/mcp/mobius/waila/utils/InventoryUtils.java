package mcp.mobius.waila.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtils {

    public static NBTTagList invToNBT(IItemHandler itemHandler, int amount) {
        NBTTagList nbtTagList = new NBTTagList();

        List<ItemStack> compressed = compressInventory(itemHandler);
        for (int i = 0; i < compressed.size(); i++) {
            if (i > amount)
                break;
            ItemStack stack = compressed.get(i);
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

    public static List<ItemStack> compressInventory(IItemHandler itemHandler) {
        List<ItemStack> compressed = new ArrayList<>();
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.isEmpty())
                continue;

            addStack(compressed, stack);
        }

        return compressed;
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
