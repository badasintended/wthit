package mcp.mobius.waila.addons.core;

import com.google.common.base.Strings;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.handlers.VanillaTooltipHandler;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.ITALIC;

public class HUDHandlerFluids implements IWailaDataProvider {

    static final IWailaDataProvider INSTANCE = new HUDHandlerFluids();

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return getStackFromLiquid(accessor.getBlockState());
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Pair<Fluid, Boolean> fluidPair = getFluidFromBlock(accessor.getBlockState());
        String name = null;
        try {
            String s = String.format(VanillaTooltipHandler.objectNameWrapper, fluidPair.getLeft().getLocalizedName(new FluidStack(fluidPair.getLeft(), 1000)));
            if (s != null && !s.endsWith("Unnamed"))
                name = s;

            if (name != null)
                currenttip.add(name);
        } catch (Exception e) {
        }

        if (currenttip.size() == 0)
            currenttip.add("< Unnamed >");
        else {
            if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATA, true))
                currenttip.add(String.format(ITALIC + "[%s:%d]", accessor.getBlock().getRegistryName().toString(), accessor.getMetadata()));
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Pair<Fluid, Boolean> fluidPair = getFluidFromBlock(accessor.getBlockState());
        String modName = ModIdentification.findModContainer(FluidRegistry.getDefaultFluidName(fluidPair.getLeft()).split(":")[0]).getName();

        if (!Strings.isNullOrEmpty(modName))
            currenttip.add(String.format(VanillaTooltipHandler.modNameWrapper, modName));

        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        return null;
    }

    private static ItemStack getStackFromLiquid(IBlockState state) {
        Pair<Fluid, Boolean> fluidPair = getFluidFromBlock(state);
        Fluid fluid = fluidPair.getLeft();
        boolean vanilla = fluidPair.getRight();
        ItemStack ret = null;

        if (fluid != null) {
            if (FluidRegistry.isUniversalBucketEnabled())
                ret = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluid);
            else if (vanilla)
                ret = fluid == FluidRegistry.WATER ? new ItemStack(Items.WATER_BUCKET) : new ItemStack(Items.LAVA_BUCKET);
            else
                ret = FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, 1000), new ItemStack(Items.BUCKET));
        }
        return ret != null ? ret : null;
    }

    private static Pair<Fluid, Boolean> getFluidFromBlock(IBlockState state) {
        Fluid fluid = null;
        boolean vanilla = false;
        if (state.getBlock() instanceof BlockLiquid) {
            Block fluidBlock = BlockLiquid.getStaticBlock(state.getMaterial());
            fluid = fluidBlock == Blocks.WATER ? FluidRegistry.WATER : FluidRegistry.LAVA;
            vanilla = true;
        } else if (state.getBlock() instanceof IFluidBlock) fluid = ((IFluidBlock) state.getBlock()).getFluid();


        return Pair.of(fluid, vanilla);
    }
}
