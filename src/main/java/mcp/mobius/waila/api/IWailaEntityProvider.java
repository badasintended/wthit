package mcp.mobius.waila.api;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface IWailaEntityProvider {
	
	/* A way to get an override on the entity returned by the raytracing */
	Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config);
	
	/* The classical HEAD/BODY/TAIL text getters */
	List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config);
	List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config);
	List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config);
	
	/* This method is used server side to return a custom NBT tag for tes properly registered with the Registrar.registerNBTProvider.
	 * It is an override to the classical way of getting NBT data via writeToNBT.
	 * NBTTagCompound tag argument is the current NBT tag passed along by the various providers (first provider will be empty).
	 * You HAVE TO return it if you registered as a provider, even if you are not doing anything to it (similar to the tooltip lists).
	 * */
	NBTTagCompound getNBTData(Entity ent, NBTTagCompound tag);	
}
