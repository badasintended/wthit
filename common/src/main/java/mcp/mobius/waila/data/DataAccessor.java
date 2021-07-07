package mcp.mobius.waila.data;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

// TODO: Remove IDataAccessor interface
public enum DataAccessor implements ICommonAccessor, IBlockAccessor, IDataAccessor, IEntityAccessor {

    INSTANCE;

    private World world;
    private PlayerEntity player;
    private HitResult hitResult;
    private Vec3d renderingVec = null;
    private Block block = Blocks.AIR;
    private BlockState state = Blocks.AIR.getDefaultState();
    private BlockPos pos = BlockPos.ORIGIN;
    private Identifier blockRegistryName = Registry.ITEM.getDefaultId();
    private BlockEntity blockEntity;
    private Entity entity;
    private NbtCompound serverData = null;
    private long timeLastUpdate = System.currentTimeMillis();
    private double partialFrame;
    private ItemStack stack = ItemStack.EMPTY;

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public PlayerEntity getPlayer() {
        return this.player;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    @Override
    public BlockState getBlockState() {
        return this.state;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public BlockPos getPosition() {
        return this.pos;
    }

    @Override
    public HitResult getHitResult() {
        return this.hitResult;
    }

    @Override
    public Vec3d getRenderingPosition() {
        return this.renderingVec;
    }

    @Override
    public NbtCompound getServerData() {
        if ((this.blockEntity != null) && this.isTagCorrectBlockEntity(this.serverData))
            return serverData;

        if ((this.entity != null) && this.isTagCorrectEntity(this.serverData))
            return serverData;

        if (this.blockEntity != null)
            return blockEntity.writeNbt(new NbtCompound());

        if (this.entity != null)
            return entity.writeNbt(new NbtCompound());

        return new NbtCompound();
    }

    @Override
    public double getPartialFrame() {
        return this.partialFrame;
    }

    @Override
    public Direction getSide() {
        return hitResult == null ? null : hitResult.getType() == HitResult.Type.ENTITY ? null : ((BlockHitResult) hitResult).getSide();
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public String getModNameFormat() {
        return Waila.config.get().getFormatting().getModName();
    }

    @Override
    public String getBlockNameFormat() {
        return Waila.config.get().getFormatting().getBlockName();
    }

    @Override
    public String getFluidNameFormat() {
        return Waila.config.get().getFormatting().getFluidName();
    }

    @Override
    public String getEntityNameFormat() {
        return Waila.config.get().getFormatting().getEntityName();
    }

    @Override
    public String getRegistryNameFormat() {
        return Waila.config.get().getFormatting().getRegistryName();
    }

    @Override
    public Identifier getBlockId() {
        return blockRegistryName;
    }

    public void set(World world, PlayerEntity player, HitResult hit, Entity viewEntity, double partialTicks) {
        this.world = world;
        this.player = player;
        this.hitResult = hit;

        if (this.hitResult.getType() == HitResult.Type.BLOCK) {
            this.pos = ((BlockHitResult) hit).getBlockPos();
            this.blockEntity = this.world.getBlockEntity(this.pos);
            this.entity = null;
            setState(world.getBlockState(pos));
        } else if (this.hitResult.getType() == HitResult.Type.ENTITY) {
            this.entity = ((EntityHitResult) hit).getEntity();
            this.pos = new BlockPos(entity.getPos());
            this.blockEntity = null;
            setState(Blocks.AIR.getDefaultState());
        }

        if (viewEntity != null) {
            double px = viewEntity.prevX + (viewEntity.getX() - viewEntity.prevX) * partialTicks;
            double py = viewEntity.prevY + (viewEntity.getY() - viewEntity.prevY) * partialTicks;
            double pz = viewEntity.prevZ + (viewEntity.getZ() - viewEntity.prevZ) * partialTicks;
            this.renderingVec = new Vec3d(this.pos.getX() - px, this.pos.getY() - py, this.pos.getZ() - pz);
            this.partialFrame = partialTicks;
        }
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setServerData(NbtCompound tag) {
        this.serverData = tag;
    }

    public void setState(BlockState state) {
        this.state = state;
        this.block = state.getBlock();
        this.stack = block.getPickStack(world, pos, state);
        this.blockRegistryName = Registry.BLOCK.getId(block);
    }

    private boolean isTagCorrectBlockEntity(NbtCompound tag) {
        if (tag == null) {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }

        int x = tag.getInt("x");
        int y = tag.getInt("y");
        int z = tag.getInt("z");

        BlockPos hitPos = ((BlockHitResult) hitResult).getBlockPos();
        if (x == hitPos.getX() && y == hitPos.getY() && z == hitPos.getZ())
            return true;
        else {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }
    }

    private boolean isTagCorrectEntity(NbtCompound tag) {
        if (tag == null || !tag.contains("WailaEntityID")) {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }

        int id = tag.getInt("WailaEntityID");

        if (id == this.entity.getId())
            return true;
        else {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }
    }

    public boolean isTimeElapsed(long time) {
        return System.currentTimeMillis() - this.timeLastUpdate >= time;
    }

    public void resetTimer() {
        this.timeLastUpdate = System.currentTimeMillis();
    }

}
