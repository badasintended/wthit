package mcp.mobius.waila.access;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public enum DataAccessor implements ICommonAccessor, IBlockAccessor, IEntityAccessor {

    INSTANCE;

    private Level world;
    private Player player;
    private HitResult hitResult;
    private Vec3 renderingVec = null;
    private Block block = Blocks.AIR;
    private BlockState state = Blocks.AIR.defaultBlockState();
    private BlockPos pos = BlockPos.ZERO;
    private ResourceLocation blockRegistryName = Registry.ITEM.getDefaultKey();
    private BlockEntity blockEntity;
    private Entity entity;
    private CompoundTag serverData = null;
    private long timeLastUpdate = System.currentTimeMillis();
    private double partialFrame;
    private ItemStack stack = ItemStack.EMPTY;

    @Override
    public Level getWorld() {
        return this.world;
    }

    @Override
    public Player getPlayer() {
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
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> T getBlockEntity() {
        return (T) this.blockEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> T getEntity() {
        return (T) this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
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
    public Vec3 getRenderingPosition() {
        return this.renderingVec;
    }

    @Override
    public CompoundTag getServerData() {
        if ((this.blockEntity != null) && this.isTagCorrectBlockEntity(this.serverData))
            return serverData;

        if ((this.entity != null) && this.isTagCorrectEntity(this.serverData))
            return serverData;

        return new CompoundTag();
    }

    @Override
    public long getServerDataTime() {
        CompoundTag data = getServerData();
        return data.contains("WailaTime") ? data.getLong("WailaTime") : System.currentTimeMillis();
    }

    public void setServerData(CompoundTag tag) {
        this.serverData = tag;
    }

    @Override
    public double getPartialFrame() {
        return this.partialFrame;
    }

    @Override
    public Direction getSide() {
        return hitResult == null ? null : hitResult.getType() == HitResult.Type.ENTITY ? null : ((BlockHitResult) hitResult).getDirection();
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public ResourceLocation getBlockId() {
        return blockRegistryName;
    }

    public void set(Level world, Player player, HitResult hit, Entity viewEntity, double partialTicks) {
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
            this.pos = new BlockPos(entity.position());
            this.blockEntity = null;
            setState(Blocks.AIR.defaultBlockState());
        }

        if (viewEntity != null) {
            double px = viewEntity.xo + (viewEntity.getX() - viewEntity.xo) * partialTicks;
            double py = viewEntity.yo + (viewEntity.getY() - viewEntity.yo) * partialTicks;
            double pz = viewEntity.zo + (viewEntity.getZ() - viewEntity.zo) * partialTicks;
            this.renderingVec = new Vec3(this.pos.getX() - px, this.pos.getY() - py, this.pos.getZ() - pz);
            this.partialFrame = partialTicks;
        }
    }

    public void setState(BlockState state) {
        this.state = state;
        this.block = state.getBlock();
        this.stack = block.getCloneItemStack(world, pos, state);
        this.blockRegistryName = Registry.BLOCK.getKey(block);
    }

    private boolean isTagCorrectBlockEntity(CompoundTag tag) {
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

    private boolean isTagCorrectEntity(CompoundTag tag) {
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
