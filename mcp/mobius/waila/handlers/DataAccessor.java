package mcp.mobius.waila.handlers;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class DataAccessor implements IWailaDataAccessor {

	World world;
	EntityPlayer player;
	MovingObjectPosition mop;
	Block block;
	int blockID;
	int metadata;
	TileEntity entity;
	NBTTagCompound nbtdata = null;
	
	public DataAccessor(World _world, EntityPlayer _player, MovingObjectPosition _mop) {
		this.world    = _world;
		this.player   = _player;
		this.mop      = _mop;
		this.blockID  = world.getBlockId(_mop.blockX, _mop.blockY, _mop.blockZ);
		this.block    = Block.blocksList[this.blockID];
		this.metadata = world.getBlockMetadata(_mop.blockX, _mop.blockY, _mop.blockZ);
		this.entity   = world.getBlockTileEntity(_mop.blockX, _mop.blockY, _mop.blockZ);
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
	public Block getBlock() {
		return this.block;
	}

	@Override
	public int getMetadata() {
		return this.metadata;
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
		return this.nbtdata;
	}

	@Override
	public int getBlockID() {
		return this.blockID;
	}

}
