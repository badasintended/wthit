package mcp.mobius.waila.addons.core;

import com.google.common.base.Strings;
import mcp.mobius.waila.addons.HUDHandlerBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class HUDHandlerFluids extends HUDHandlerBase {

    static final IWailaDataProvider INSTANCE = new HUDHandlerFluids();

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return getStackFromLiquid(accessor.getBlockState(), accessor);
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Pair<Fluid, Boolean> fluidPair = getFluidFromBlock(accessor.getBlockState());

        String name = null;
        String displayName = String.format(FormattingConfig.fluidFormat, fluidPair.getLeft().getLocalizedName(new FluidStack(fluidPair.getLeft(), 1000)));
        if (displayName != null && !displayName.endsWith("Unnamed"))
            name = displayName;

        if (name != null)
            currenttip.add("\u00a7r" + name);

        if (currenttip.isEmpty())
            currenttip.add("\u00a7r" + String.format(FormattingConfig.fluidFormat, "< Unnamed >"));
        else if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATA, true) && !Strings.isNullOrEmpty(FormattingConfig.metaFormat))
            currenttip.add(String.format(FormattingConfig.metaFormat, accessor.getBlock().getRegistryName().toString(), accessor.getMetadata()));

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Pair<Fluid, Boolean> fluidPair = getFluidFromBlock(accessor.getBlockState());
        String modName = ModIdentification.findModContainer(FluidRegistry.getDefaultFluidName(fluidPair.getLeft()).split(":")[0]).getName();
        if (!Strings.isNullOrEmpty(FormattingConfig.modNameFormat) && !Strings.isNullOrEmpty(modName))
            currenttip.add(String.format(FormattingConfig.modNameFormat, modName));

        return currenttip;
    }

    private static ItemStack getStackFromLiquid(IBlockState state, IWailaDataAccessor accessor) {
        Pair<Fluid, Boolean> fluidPair = getFluidFromBlock(state);
        Fluid fluid = fluidPair.getLeft();
        boolean vanilla = fluidPair.getRight();
        ItemStack ret = ItemStack.EMPTY;
        if (fluid != null) {
            if (FluidRegistry.isUniversalBucketEnabled())
                ret = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluid);
            else if (vanilla)
                ret = fluid == FluidRegistry.WATER ? new ItemStack(Items.WATER_BUCKET) : new ItemStack(Items.LAVA_BUCKET);
            else {
                IFluidHandler dummyFluid = new FluidBlockWrapper((IFluidBlock) fluid.getBlock(), accessor.getWorld(), accessor.getPosition());
                ret = FluidUtil.tryFillContainer(new ItemStack(Items.BUCKET), dummyFluid, 1000, null, true).getResult();
            }
        }

        return ret;
    }

    private static Pair<Fluid, Boolean> getFluidFromBlock(IBlockState state) {
        Fluid fluid = null;
        boolean vanilla = false;
        if (state.getBlock() instanceof BlockLiquid) {
            Block fluidBlock = BlockLiquid.getStaticBlock(state.getMaterial());
            fluid = fluidBlock == Blocks.WATER ? FluidRegistry.WATER : FluidRegistry.LAVA;
            vanilla = true;
        }
        else if (state.getBlock() instanceof IFluidBlock) fluid = ((IFluidBlock) state.getBlock()).getFluid();

        return Pair.of(fluid, vanilla);
    }
}
