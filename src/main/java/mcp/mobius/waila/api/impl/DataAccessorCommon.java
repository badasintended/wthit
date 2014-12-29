package mcp.mobius.waila.api.impl;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaEntityAccessor;

public class DataAccessorCommon implements IWailaCommonAccessor, IWailaDataAccessor, IWailaEntityAccessor{

	public World world;
	public EntityPlayer player;
	public MovingObjectPosition mop;
	public Vec3 renderingvec = null;
	public Block block;
	public int blockID;
	public int metadata;
	public TileEntity tileEntity;
	public Entity entity;
	public NBTTagCompound remoteNbt = null;
	public long timeLastUpdate = System.currentTimeMillis();
	public double partialFrame;
	public ItemStack stack;	
	
	public static DataAccessorCommon instance = new DataAccessorCommon();
	
	public void set(World _world, EntityPlayer _player, MovingObjectPosition _mop) {
		this.set(_world, _player, _mop, null, 0.0);
	}
	
	public void set(World _world, EntityPlayer _player, MovingObjectPosition _mop, EntityLivingBase viewEntity, double partialTicks) {
		this.world      = _world;
		this.player     = _player;
		this.mop        = _mop;
		
		if (this.mop.typeOfHit == MovingObjectType.BLOCK){
			//this.blockID  = world.getBlockId(_mop.blockX, _mop.blockY, _mop.blockZ);
			//this.block    = Block.blocksList[this.blockID];
			this.block      = world.getBlock(_mop.blockX, _mop.blockY, _mop.blockZ);
			this.metadata   = world.getBlockMetadata(_mop.blockX, _mop.blockY, _mop.blockZ);
			this.tileEntity = world.getTileEntity(_mop.blockX, _mop.blockY, _mop.blockZ);
			try{ this.stack = new ItemStack(this.block, 1, this.metadata); } catch (Exception e) {}
		} else if (this.mop.typeOfHit == MovingObjectType.ENTITY){
			this.entity   = _mop.entityHit;
		}
		
		if (viewEntity != null){
			double px = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
			double py = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
			double pz = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;		
			this.renderingvec = Vec3.createVectorHelper(_mop.blockX - px, _mop.blockY - py, _mop.blockZ - pz);
			this.partialFrame = partialTicks;
		}
	}	
	
	@Override
	public World getWorld() {return null;}

	@Override
	public EntityPlayer getPlayer() {return null;}

	@Override
	public Block getBlock() {return null;}

	@Override
	public int getBlockID() {return 0;}

	@Override
	public int getMetadata() {return 0;}

	@Override
	public TileEntity getTileEntity() {return null;}

	@Override
	public Entity getEntity() {return null;}

	@Override
	public MovingObjectPosition getPosition() {return null;}

	@Override
	public Vec3 getRenderingPosition() {return null;}

	@Override
	public NBTTagCompound getNBTData() {return null;}

	@Override
	public int getNBTInteger(NBTTagCompound tag, String keyname) {return 0;}

	@Override
	public double getPartialFrame() {return 0;}

	@Override
	public ForgeDirection getSide() {return null;}

	@Override
	public ItemStack getStack() {return null;}

}
