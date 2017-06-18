package mcp.mobius.waila.addons.capability;

import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import java.util.List;

public class HUDHandlerEnergy implements IWailaDataProvider {

    static final IWailaDataProvider INSTANCE = new HUDHandlerEnergy();

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (!config.getConfig("capability.energyinfo") || accessor.getTileEntity() == null)
            return currenttip;

        if (accessor.getNBTData().hasKey("forgeEnergy") && accessor.getTileEntity().hasCapability(CapabilityEnergy.ENERGY, accessor.getSide())) {
            NBTTagCompound energyTag = accessor.getNBTData().getCompoundTag("forgeEnergy");
            int stored = energyTag.getInteger("stored");
            int capacity = energyTag.getInteger("capacity");

            ((ITaggedList<String, String>) currenttip).add(String.format("%d / %d FE", stored, capacity), "IEnergyStorage");
            return currenttip;
        }

        return currenttip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te != null) {
            if (te.hasCapability(CapabilityEnergy.ENERGY, null)) {
                IEnergyStorage energyStorage = te.getCapability(CapabilityEnergy.ENERGY, null);
                NBTTagCompound energyTag = new NBTTagCompound();
                energyTag.setInteger("capacity", energyStorage.getMaxEnergyStored());
                energyTag.setInteger("stored", energyStorage.getEnergyStored());
                tag.setTag("forgeEnergy", energyTag);
            }
        }
        return tag;
    }
}
