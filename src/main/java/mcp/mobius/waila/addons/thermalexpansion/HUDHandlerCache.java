package mcp.mobius.waila.addons.thermalexpansion;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.registry.GameData;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class HUDHandlerCache implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) { return null; }

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		if(!config.getConfig("thermalexpansion.cache"))
            return currenttip;
        try {
            ItemStack storedItem = null;
            if(accessor.getNBTData().hasKey("Item"))
                storedItem = readItemStack(accessor.getNBTData().getCompoundTag("Item"));

            String name = currenttip.get(0);
            String color = "";
            if(name.startsWith("\u00a7"))
                color = name.substring(0, 2);

            if(storedItem != null) {
                String namex = String.valueOf(GameData.getItemRegistry().getNameForObject(storedItem.getItem()));
                name += String.format(" < " + SpecialChars.getRenderString("waila.stack", "1", namex, "0", String.valueOf(storedItem.getItemDamage())) + color + " %s >", storedItem.getDisplayName());
            }
            else
                name += " " + LangUtil.translateG("hud.msg.empty");

            currenttip.set(0, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
        if (!config.getConfig("thermalexpansion.cache"))
            return currenttip;

        NBTTagCompound tag = accessor.getNBTData();
        ItemStack storedItem = null;
        if(tag.hasKey("Item"))
            storedItem = readItemStack(tag.getCompoundTag("Item"));

        int stored = 0;
        int maxStored = 0;
        if(tag.hasKey("Stored"))
            stored = tag.getInteger("Stored");
        if(tag.hasKey("MaxStored"))
            maxStored = tag.getInteger("MaxStored");

        if(storedItem != null) {
            currenttip.add("Stored: " + stored + "/" + maxStored);
        }
        else
            currenttip.add("Capacity: " + maxStored);


		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if(te != null)
            te.writeToNBT(tag);
        try {
            tag.setInteger("MaxStored", (Integer) ThermalExpansionModule.TileCache_getMaxStored.invoke(te));
            tag.setInteger("Stored", (Integer) ThermalExpansionModule.TileCache_getStored.invoke(te));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return tag;
	}

    public ItemStack readItemStack(NBTTagCompound tag) {
        ItemStack is = new ItemStack(Item.getItemById(tag.getShort("id")));
        is.stackSize = tag.getInteger("Count");
        is.setItemDamage(Math.max(0, tag.getShort("Damage")));
        if (tag.hasKey("tag", 10)) {
            //is.stackTagCompound = tag.getCompoundTag("tag"); //TODO
        }

        return is;
    }
	
}
