package mcp.mobius.waila.addons.thermaldynamics;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * Created by Lordmau5 on 28.02.2015.
 */
public class HUDHandlerDuct implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
        if (!config.getConfig("thermaldynamics.fluiductsFluid")) return currenttip;

        FluidStack fluid = FluidStack.loadFluidStackFromNBT(accessor.getNBTData());

        String name = "";

        try{
            name += String.format(" < %s >", fluid.getFluid().getLocalizedName(fluid));
        } catch (NullPointerException f){
            name += " " + LangUtil.translateG("hud.msg.empty");
        }

        currenttip.add(name);
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
        if (!config.getConfig("thermaldynamics.fluiductsAmount")) return currenttip;

        int amount = 0;

        NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("Amount"))
            amount = accessor.getNBTInteger(tag, "Amount");

        currenttip.add(String.format(" %d / 1000 mB", amount));

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te != null)
            te.writeToNBT(tag);
        return tag;
    }

}
