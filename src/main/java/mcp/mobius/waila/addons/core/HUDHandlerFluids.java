package mcp.mobius.waila.addons.core;

import com.google.common.base.Strings;
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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class HUDHandlerFluids implements IWailaDataProvider {

    static final IWailaDataProvider INSTANCE = new HUDHandlerFluids();

    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return getStackFromLiquid(accessor.getBlockState(), accessor);
    }

    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Fluid fluid = getFluidFromBlock(accessor.getBlockState());
        if (fluid == null)
            return currenttip;

        String name = null;
        String displayName = String.format(FormattingConfig.fluidFormat, fluid.getLocalizedName(new FluidStack(fluid, 1000)));
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

    @Nonnull
    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Fluid fluid = getFluidFromBlock(accessor.getBlockState());
        if (fluid == null)
            return currenttip;

        String modName = ModIdentification.findModContainer(FluidRegistry.getDefaultFluidName(fluid).split(":")[0]).getName();
        if (!Strings.isNullOrEmpty(FormattingConfig.modNameFormat) && !Strings.isNullOrEmpty(modName))
            currenttip.add(String.format(FormattingConfig.modNameFormat, modName));

        return currenttip;
    }

    @Nonnull
    private static ItemStack getStackFromLiquid(IBlockState state, IWailaDataAccessor accessor) {
        IFluidHandler fluidHandler = FluidUtil.getFluidHandler(accessor.getWorld(), accessor.getPosition(), accessor.getSide());
        if (fluidHandler == null)
            return ItemStack.EMPTY;

        FluidStack stack = fluidHandler.drain(Fluid.BUCKET_VOLUME, false);
        if (stack == null)
            return ItemStack.EMPTY;

        ItemStack bucket = FluidUtil.getFilledBucket(stack);
        if (!bucket.isEmpty())
            return bucket;

        FluidActionResult result = FluidUtil.tryFillContainer(new ItemStack(Items.BUCKET), fluidHandler, 1, null, false);
        return result.isSuccess() ? result.getResult() : ItemStack.EMPTY;
    }

    @Nullable
    private static Fluid getFluidFromBlock(IBlockState state) {
        Fluid fluid = null;
        if (state.getBlock() instanceof BlockLiquid) {
            Block fluidBlock = BlockLiquid.getStaticBlock(state.getMaterial());
            fluid = fluidBlock == Blocks.WATER ? FluidRegistry.WATER : FluidRegistry.LAVA;
        } else if (state.getBlock() instanceof IFluidBlock) fluid = ((IFluidBlock) state.getBlock()).getFluid();

        return fluid;
    }
}
