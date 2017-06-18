package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class HUDHandlerFurnace implements IWailaDataProvider {

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (!config.getConfig("vanilla.furnacedisplay") || accessor.getBlock() != Blocks.LIT_FURNACE)
            return currenttip;

        int cookTime = accessor.getNBTData().getShort("CookTime");

        NBTTagList itemTag = accessor.getNBTData().getTagList("Items", 10);
        ItemStack[] inventory = new ItemStack[3];
        for (int i = 0; i < itemTag.tagCount(); i++) {
            NBTTagCompound tagCompound = itemTag.getCompoundTagAt(i);
            byte slot = tagCompound.getByte("Slot");
            ItemStack stack = new ItemStack(tagCompound);
            inventory[slot] = stack;
        }

        String renderStr = "";

        if (inventory[0] != null) {
            String name = inventory[0].getItem().getRegistryName().toString();
            renderStr += SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(inventory[0].getCount()), String.valueOf(inventory[0].getItemDamage()));
        } else renderStr += SpecialChars.getRenderString("waila.stack", "2");

        if (inventory[1] != null) {
            String name = inventory[1].getItem().getRegistryName().toString();
            renderStr += SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(inventory[1].getCount()), String.valueOf(inventory[1].getItemDamage()));
        } else renderStr += SpecialChars.getRenderString("waila.stack", "2");

        renderStr += SpecialChars.getRenderString("waila.progress", String.valueOf(cookTime), String.valueOf(200));

        if (inventory[2] != null) {
            String name = inventory[2].getItem().getRegistryName().toString();
            renderStr += SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(inventory[2].getCount()), String.valueOf(inventory[2].getItemDamage()));
        } else renderStr += SpecialChars.getRenderString("waila.stack", "2");

        currenttip.add(renderStr);

        return currenttip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        return te.writeToNBT(tag);
    }

    public static void register(IWailaRegistrar registrar) {
        registrar.addConfig("VanillaMC", "vanilla.furnacedisplay", true);

        HUDHandlerFurnace provider = new HUDHandlerFurnace();

        registrar.registerBodyProvider(provider, TileEntityFurnace.class);
        registrar.registerNBTProvider(provider, TileEntityFurnace.class);
    }
}
