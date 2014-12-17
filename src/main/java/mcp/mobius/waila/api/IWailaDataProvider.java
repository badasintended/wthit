package mcp.mobius.waila.api;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface IWailaDataProvider{
	/*
	 *  Use this method to return an item stack in case the default lookup system fails.
	 *  Return null if you want to use the default lookup system.
	 *  You get the world, the player and the location of the block. With that, it is easy to gather information & tile entities
	 */
	ItemStack    getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config);
	
	/* Waila HUD is divided into 3 zones. The head corresponds to the item name, 
	 * body to where you mostly want to put informations, and I reserve the tail for modname display 
	 */ 
	
	/* Those 3 methods works exactly the same way, except they are related to a different zone in Waila HUD.
	 * You get in input world, player and the block location. You also get the itemstack as returned by the default lookup system or getWailaStack().
	 * ConfigHandler provides the current Waila config state so you can show/hide elements depending on the configuration. Refer the ConfigHandler class for more info.
	 * currenttip represents the current list of text lines in the tooltip zone.
	 * For example, getWailaHead() will have the current item name as currenttip.
	 * You can modify the tips, add more, remove some, etc.
	 * When you are done, just returns the currenttip and it will display in Waila.
	 * 
	 * Always return the currenttip is you don't want to modify the current zone.
	 */
	
	List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config);
	List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config);
	List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config);
	
	/* This method is used server side to return a custom NBT tag for tes properly registered with the Registrar.registerNBTProvider.
	 * It is an override to the classical way of getting NBT data via writeToNBT.
	 * NBTTagCompound tag argument is the current NBT tag passed along by the various providers (first provider will be empty).
	 * You HAVE TO return it if you registered as a provider, even if you are not doing anything to it (similar to the tooltip lists).
	 * */
	NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z);
}
