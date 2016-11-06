package mcp.mobius.waila.addons.capability;

import mcp.mobius.waila.addons.HUDHandlerBase;
import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import java.util.List;

public class HUDHandlerTank extends HUDHandlerBase {

    static final IWailaDataProvider INSTANCE = new HUDHandlerTank();

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (!config.getConfig("capability.tankinfo"))
            return currenttip;

        TileEntity tile = accessor.getTileEntity();
        if (tile == null || !tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
            return currenttip;

        NBTTagList tanks = accessor.getNBTData().getTagList("tankInfo", 10);
        int fluidCount = 0;
        for (int i = 0; i < tanks.tagCount(); i++) {
            NBTTagCompound tankInfo = tanks.getCompoundTagAt(i);
            FluidStack contents = FluidStack.loadFluidStackFromNBT(tankInfo.getCompoundTag("fluid"));
            int capacity = tankInfo.getInteger("capacity");

            if (fluidCount <= 5) {
                ((ITaggedList<String, String>) currenttip).add(String.format("%s: %d / %d mB", contents.getLocalizedName(), contents.amount, capacity), "IFluidHandler");
                fluidCount++;
            } else ((ITaggedList<String, String>) currenttip).add(I18n.translateToLocal("hud.msg.toomuch"), "IFluidHandler");
        }

        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te != null) {
            te.writeToNBT(tag);

            if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                NBTTagList tanks = new NBTTagList();
                IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                for (IFluidTankProperties property : fluidHandler.getTankProperties()) {
                    if (property.getContents() != null) {
                        NBTTagCompound tankInfo = new NBTTagCompound();
                        tankInfo.setTag("fluid", property.getContents().writeToNBT(new NBTTagCompound()));
                        tankInfo.setInteger("capacity", property.getCapacity());
                        tanks.appendTag(tankInfo);
                    }
                }

                tag.setTag("tankInfo", tanks);
            }
        }
        return tag;
    }
}
