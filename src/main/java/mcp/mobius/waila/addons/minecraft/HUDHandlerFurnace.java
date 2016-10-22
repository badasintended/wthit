package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.List;

public class HUDHandlerFurnace implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        int cookTime = accessor.getNBTData().getShort("CookTime");
        if (!accessor.getNBTData().hasKey("Items"))
            return currenttip;

        if (!config.getConfig("vanilla.furnacedisplay"))
            return currenttip;

        NBTTagList tag = accessor.getNBTData().getTagList("Items", 10);

        String renderStr = "";
        if (tag.getCompoundTagAt(0) != null) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTagAt(0));
            if (stack != null) {
                String name = stack.getItem().getRegistryName().toString();
                renderStr += SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(stack.stackSize), String.valueOf(stack.getItemDamage()));
            }
        }
        if (tag.getCompoundTagAt(1) != null) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTagAt(1));
            if (stack != null) {
                String name = stack.getItem().getRegistryName().toString();
                renderStr += SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(stack.stackSize), String.valueOf(stack.getItemDamage()));
            }
        }

        renderStr += SpecialChars.getRenderString("waila.progress", String.valueOf(cookTime), String.valueOf(200));
        if (tag.getCompoundTagAt(2) != null)         {
            ItemStack stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTagAt(2));
            if (stack != null) {
                String name = stack.getItem().getRegistryName().toString();
                renderStr += SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(stack.stackSize), String.valueOf(stack.getItemDamage()));
            }
        }

        currenttip.add(renderStr);

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te != null)
            te.writeToNBT(tag);
        return tag;
    }

    public static void register(IWailaRegistrar registrar) {
        registrar.addConfig("VanillaMC", "vanilla.furnacedisplay", true);

        registrar.registerBodyProvider(new HUDHandlerFurnace(), TileEntityFurnace.class);
        registrar.registerNBTProvider(new HUDHandlerFurnace(), TileEntityFurnace.class);

    }
}
