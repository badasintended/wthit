package mcp.mobius.waila.api.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaFMPAccessor;
import mcp.mobius.waila.utils.NBTUtil;

public class DataAccessorFMP implements IWailaFMPAccessor {
	
	public String id;
	public World world;
	public EntityPlayer player;
	public MovingObjectPosition mop;
	public Vec3 renderingvec = null;
	public TileEntity entity;
	public NBTTagCompound partialNBT = null;	
	public NBTTagCompound remoteNBT  = null;
	public long timeLastUpdate = System.currentTimeMillis();
	public double partialFrame;
	
	public static DataAccessorFMP instance = new DataAccessorFMP();
	
	public void set(World _world, EntityPlayer _player, MovingObjectPosition _mop, NBTTagCompound _partialNBT, String id) {
		this.set(_world, _player, _mop, _partialNBT, id, null, 0.0);
	}
	
	public void set(World _world, EntityPlayer _player, MovingObjectPosition _mop, NBTTagCompound _partialNBT, String id, EntityLivingBase viewEntity, double partialTicks) {
		this.world      = _world;
		this.player     = _player;
		this.mop        = _mop;
		this.entity     = world.getBlockTileEntity(_mop.blockX, _mop.blockY, _mop.blockZ);
		this.partialNBT = _partialNBT;
		this.id         = id;
		
		if (viewEntity != null){
			double px = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
			double py = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
			double pz = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;		
			this.renderingvec = Vec3.createVectorHelper(_mop.blockX - px, _mop.blockY - py, _mop.blockZ - pz);
			this.partialFrame = partialTicks;
		}
	}	
	
	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public EntityPlayer getPlayer() {
		return this.player;
	}

	@Override
	public TileEntity getTileEntity() {
		return this.entity;
	}

	@Override
	public MovingObjectPosition getPosition() {
		return this.mop;
	}

	@Override
	public NBTTagCompound getNBTData() {
		return this.partialNBT;
	}

	@Override
	public NBTTagCompound getFullNBTData() {
		if (this.entity == null) return null;
		
		if (this.isTagCorrect(this.remoteNBT))
			return this.remoteNBT;

		NBTTagCompound tag = new NBTTagCompound();
		this.entity.writeToNBT(tag);
		return tag;
	}

	@Override
	public int getNBTInteger(NBTTagCompound tag, String keyname) {
		return NBTUtil.getNBTInteger(tag, keyname);
	}

	@Override
	public double getPartialFrame() {
		return this.partialFrame;
	}

	@Override
	public Vec3 getRenderingPosition() {
		return this.renderingvec;
	}

	@Override
	public String getID() {
		return this.id;
	}		
	
	private boolean isTagCorrect(NBTTagCompound tag){
		if (tag == null){
			this.timeLastUpdate = System.currentTimeMillis() - 250;
			return false;
		}
		
		int x = tag.getInteger("x");
		int y = tag.getInteger("y");
		int z = tag.getInteger("z");
		
		if (x == this.mop.blockX && y == this.mop.blockY && z == this.mop.blockZ)
			return true;
		else {
			this.timeLastUpdate = System.currentTimeMillis() - 250;			
			return false;
		}
	}
}
