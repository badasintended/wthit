package mcp.mobius.waila.handlers;

import com.google.common.base.Strings;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.overlay.DisplayUtil;
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

import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDHandlerBlocks implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return getStackFromLiquid(accessor.getStack(), accessor.getBlockState());
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            itemStack = getStackFromLiquid(itemStack, accessor.getBlockState());

        String name = null;
        try {
            String s = DisplayUtil.itemDisplayNameShort(itemStack);
            if (s != null && !s.endsWith("Unnamed"))
                name = s;

            if (name != null)
                currenttip.add(name);
        } catch (Exception e) {
        }

        if (itemStack.getItem() == Items.REDSTONE) {
            int md = accessor.getMetadata();
            String s = "" + md;
            if (s.length() < 2)
                s = " " + s;
            currenttip.set(currenttip.size() - 1, name + " " + s);
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
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        currenttip.add(RENDER + "{Plip}" + RENDER + "{Plop,thisisatest,222,333}");

        if (accessor.getBlockState().getMaterial().isLiquid())
            itemStack = getStackFromLiquid(itemStack, accessor.getBlockState());

        String modName = ModIdentification.nameFromStack(itemStack);
        if (!Strings.isNullOrEmpty(modName))
            currenttip.add(VanillaTooltipHandler.namePrefix + modName);

        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        return tag;
    }

    private static ItemStack getStackFromLiquid(ItemStack stack, IBlockState state) {
        Fluid fluid = null;
        boolean vanilla = false;
        ItemStack ret = null;
        if (state.getBlock() instanceof BlockLiquid) {
            Block fluidBlock = BlockLiquid.getStaticBlock(state.getMaterial());
            fluid = fluidBlock == Blocks.WATER ? FluidRegistry.WATER : FluidRegistry.LAVA;
            vanilla = true;
        } else if (state.getBlock() instanceof IFluidBlock) fluid = ((IFluidBlock) state.getBlock()).getFluid();

        if (fluid != null) {
            if (FluidRegistry.isUniversalBucketEnabled())
                ret = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluid);
            else if (vanilla)
                ret = fluid == FluidRegistry.WATER ? new ItemStack(Items.WATER_BUCKET) : new ItemStack(Items.LAVA_BUCKET);

            if (ret != null)
                ret.setStackDisplayName(SpecialChars.RESET + SpecialChars.WHITE + fluid.getLocalizedName(new FluidStack(fluid, 1000)));
        }
        return ret != null ? ret : stack;
    }
}
