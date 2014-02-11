package mcp.mobius.waila.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public interface IWailaEntityAccessor {
	World        		 getWorld();
	EntityPlayer 		 getPlayer();
	Entity               getEntity();
	MovingObjectPosition getPosition();
	Vec3                 getRenderingPosition();
	NBTTagCompound       getNBTData();
	int                  getNBTInteger(NBTTagCompound tag, String keyname);
	double               getPartialFrame();
}
