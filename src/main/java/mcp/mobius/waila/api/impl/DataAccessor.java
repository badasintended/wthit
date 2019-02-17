package mcp.mobius.waila.api.impl;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DataAccessor implements ICommonAccessor, IDataAccessor, IEntityAccessor {

    public static final DataAccessor INSTANCE = new DataAccessor();

    public World world;
    public EntityPlayer player;
    public RayTraceResult hitResult;
    public Vec3d renderingvec = null;
    public Block block = Blocks.AIR;
    public IBlockState state = Blocks.AIR.getDefaultState();
    public BlockPos pos = BlockPos.ORIGIN;
    public ResourceLocation blockRegistryName = Blocks.AIR.getRegistryName();
    public TileEntity tileEntity;
    public Entity entity;
    public NBTTagCompound serverData = null;
    public long timeLastUpdate = System.currentTimeMillis();
    public double partialFrame;
    public ItemStack stack = ItemStack.EMPTY;

    public void set(World world, EntityPlayer player, RayTraceResult hit) {
        this.set(world, player, hit, null, 0.0);
    }

    public void set(World world, EntityPlayer player, RayTraceResult hit, Entity viewEntity, double partialTicks) {
        this.world = world;
        this.player = player;
        this.hitResult = hit;

        if (this.hitResult.type == RayTraceResult.Type.BLOCK) {
            this.pos = this.hitResult.getBlockPos();
            this.state = this.world.getBlockState(this.pos);
            this.block = this.state.getBlock();
            this.tileEntity = this.world.getTileEntity(this.pos);
            this.entity = null;
            this.blockRegistryName = block.getRegistryName();
            this.stack = block.getPickBlock(state, hitResult, world, pos, player);
        } else if (this.hitResult.type == RayTraceResult.Type.ENTITY) {
            this.entity = hitResult.entity;
            this.pos = new BlockPos(entity);
            this.state = Blocks.AIR.getDefaultState();
            this.block = Blocks.AIR;
            this.tileEntity = null;
            this.stack = ItemStack.EMPTY;
        }

        if (viewEntity != null) {
            double px = viewEntity.prevPosX + (viewEntity.posX - viewEntity.prevPosX) * partialTicks;
            double py = viewEntity.prevPosY + (viewEntity.posY - viewEntity.prevPosY) * partialTicks;
            double pz = viewEntity.prevPosZ + (viewEntity.posZ - viewEntity.prevPosZ) * partialTicks;
            this.renderingvec = new Vec3d(this.pos.getX() - px, this.pos.getY() - py, this.pos.getZ() - pz);
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
    public Block getBlock() {
        return this.block;
    }

    @Override
    public IBlockState getBlockState() {
        return this.state;
    }

    @Override
    public TileEntity getTileEntity() {
        return this.tileEntity;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public BlockPos getPosition() {
        return this.pos;
    }

    public RayTraceResult getHitResult() {
        return this.hitResult;
    }

    @Override
    public Vec3d getRenderingPosition() {
        return this.renderingvec;
    }

    @Override
    public NBTTagCompound getServerData() {
        if ((this.tileEntity != null) && this.isTagCorrectTileEntity(this.serverData))
            return serverData;

        if ((this.entity != null) && this.isTagCorrectEntity(this.serverData))
            return serverData;

        if (this.tileEntity != null)
            return tileEntity.write(new NBTTagCompound());

        if (this.entity != null)
            return entity.writeWithoutTypeId(new NBTTagCompound());

        return new NBTTagCompound();
    }

    public void setServerData(NBTTagCompound tag) {
        this.serverData = tag;
    }

    private boolean isTagCorrectTileEntity(NBTTagCompound tag) {
        if (tag == null) {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }

        int x = tag.getInt("x");
        int y = tag.getInt("y");
        int z = tag.getInt("z");

        BlockPos hitPos = hitResult.getBlockPos();
        if (x == hitPos.getX() && y == hitPos.getY() && z == hitPos.getZ())
            return true;
        else {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }
    }

    private boolean isTagCorrectEntity(NBTTagCompound tag) {
        if (tag == null || !tag.contains("WailaEntityID")) {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }

        int id = tag.getInt("WailaEntityID");

        if (id == this.entity.getEntityId())
            return true;
        else {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }
    }

    @Override
    public double getPartialFrame() {
        return this.partialFrame;
    }

    @Override
    public EnumFacing getSide() {
        return hitResult == null ? null : hitResult.type == RayTraceResult.Type.ENTITY ? null : hitResult.sideHit;
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    public boolean isTimeElapsed(long time) {
        return System.currentTimeMillis() - this.timeLastUpdate >= time;
    }

    public void resetTimer() {
        this.timeLastUpdate = System.currentTimeMillis();
    }

    @Override
    public ResourceLocation getBlockId() {
        return blockRegistryName;
    }
}
