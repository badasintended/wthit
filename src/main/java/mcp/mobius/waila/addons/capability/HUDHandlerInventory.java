package mcp.mobius.waila.addons.capability;

import com.google.common.base.Strings;
import mcp.mobius.waila.addons.HUDHandlerBase;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.utils.InventoryUtils;
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
            ItemStackHandler itemHandler = new ItemStackHandler();
            itemHandler.setSize(handlerSize);
            InventoryUtils.populateInv(itemHandler, accessor.getNBTData().getTagList("handler", 10));

            List<ItemStack> toRender = new ArrayList<ItemStack>();
            for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                ItemStack stack = itemHandler.getStackInSlot(slot);
                if (stack.func_190926_b())
                    continue;

                InventoryUtils.addStack(toRender, stack);
            }

            String renderString = "";
            int drawnCount = 0;
            for (ItemStack stack : toRender) {
                String name = stack.getItem().getRegistryName().toString();
                if (drawnCount >= 5 && !accessor.getPlayer().isSneaking())
                    break;
                else if (drawnCount >= 5 && accessor.getPlayer().isSneaking()) {
                    currenttip.add(renderString);
                    renderString = "";
                    drawnCount = 0;
                }

                String nbt = "";
                if (stack.hasTagCompound())
                    nbt = stack.getTagCompound().toString();
                renderString += SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(stack.func_190916_E()), String.valueOf(stack.getItemDamage()), nbt);
                drawnCount += 1;
            }

            if (!Strings.isNullOrEmpty(renderString))
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
                tag.setTag("handler", InventoryUtils.invToNBT(itemHandler));
                tag.setInteger("handlerSize", itemHandler.getSlots());
            } else if (te instanceof IInventory) {
                IItemHandler itemHandler = new InvWrapper((IInventory) te);
                tag.setTag("handler", InventoryUtils.invToNBT(itemHandler));
                tag.setInteger("handlerSize", itemHandler.getSlots());
            } else if (te instanceof TileEntityEnderChest) {
                IItemHandler itemHandler = new InvWrapper(player.getInventoryEnderChest());
                tag.setTag("handler", InventoryUtils.invToNBT(itemHandler));
                tag.setInteger("handlerSize", itemHandler.getSlots());
            }
        }

        return tag;
    }
}
