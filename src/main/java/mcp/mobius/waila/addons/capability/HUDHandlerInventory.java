package mcp.mobius.waila.addons.capability;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.overlay.RayTracing;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class HUDHandlerInventory implements IWailaDataProvider {

    static final IWailaDataProvider INSTANCE = new HUDHandlerInventory();

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (!config.getConfig("capability.inventoryinfo") || accessor.getTileEntity() == null || accessor.getTileEntity().getClass() == TileEntityFurnace.class)
            return currenttip;

        if (accessor.getNBTData().hasKey("handler") && accessor.getTileEntity().hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, accessor.getSide())) {
            int handlerSize = accessor.getNBTData().getInteger("handlerSize");
            IItemHandler itemHandler = new ItemStackHandler(handlerSize);
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemHandler, accessor.getSide(), accessor.getNBTData().getTag("handler"));

            String itemLine = "";
            int drawnCount = 0;
            for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                if (drawnCount > 5 && !accessor.getPlayer().isSneaking())
                    break;
                else if (drawnCount > 5 && accessor.getPlayer().isSneaking()) {
                    currenttip.add(itemLine);
                    itemLine = "";
                    drawnCount = 0;
                }
                if (itemHandler.getStackInSlot(slot) != null) {
                    ItemStack stack = itemHandler.getStackInSlot(slot);
                    String name = stack.getItem().getRegistryName().toString();
                    itemLine += SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(stack.stackSize), String.valueOf(stack.getItemDamage()));
                    drawnCount += 1;
                }
            }

            currenttip.add(itemLine);
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te != null) {
            te.writeToNBT(tag);
            RayTraceResult rayTrace = RayTracing.rayTraceServer(player, player.capabilities.isCreativeMode ? 5.0 : 4.5);
            EnumFacing side = null;
            if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK)
                side = rayTrace.sideHit;

            if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
                IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
                tag.setTag("handler", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemHandler, side));
                tag.setInteger("handlerSize", itemHandler.getSlots());
            }
        }

        return tag;
    }
}
