package mcp.mobius.waila.access;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IDataReader;
import mcp.mobius.waila.api.IEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
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

public enum ClientAccessor implements ICommonAccessor, IBlockAccessor, IEntityAccessor {

    INSTANCE;

    private Level world;
    private Player player;
    private Vec3 castOrigin;
    private Vec3 viewVector;
    private double maxCastDistance;
    private HitResult hitResult;
    private Vec3 renderingVec = null;
    private Block block = Blocks.AIR;
    private BlockState state = Blocks.AIR.defaultBlockState();
    private BlockPos pos = BlockPos.ZERO;
    private ResourceLocation blockRegistryName = BuiltInRegistries.ITEM.getDefaultKey();
    private BlockEntity blockEntity;
    private Entity entity;
    private long timeLastUpdate = System.currentTimeMillis();
    private double partialFrame;
    private ItemStack stack = ItemStack.EMPTY;
    private int updateId;
    private boolean dataAccess = true;

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
    public Vec3 getCastOrigin() {
        return castOrigin;
    }

    @Override
    public Vec3 getViewVector() {
        return viewVector;
    }

    @Override
    public double getMaxCastDistance() {
        return maxCastDistance;
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
    @Deprecated
    public HitResult getHitResult() {
        return getRawHitResult();
    }

    public HitResult getRawHitResult() {
        return this.hitResult;
    }

    @Override
    public BlockHitResult getBlockHitResult() {
        return (BlockHitResult) hitResult;
    }

    @Override
    public EntityHitResult getEntityHitResult() {
        return (EntityHitResult) hitResult;
    }

    @Override
    public Vec3 getRenderingPosition() {
        return this.renderingVec;
    }

    @Override
    @SuppressWarnings("removal")
    public CompoundTag getServerData() {
        return getData().raw();
    }

    @Override
    public IDataReader getData() {
        if (!dataAccess) return DataReader.NOOP;

        var data = DataReader.CLIENT;
        if (!isTagCorrectBlockEntity() && !isTagCorrectEntity()) data.reset(null);
        return data;
    }

    @Override
    public long getServerDataTime() {
        var data = getData().raw();
        return data.contains("WailaTime") ? data.getLong("WailaTime") : System.currentTimeMillis();
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

    @Override
    public int getUpdateId() {
        return updateId;
    }

    public void set(Level world, Player player, Vec3 castOrigin, Vec3 viewVector, double maxCastDistance, HitResult hit, Entity viewEntity, double partialTicks) {
        this.updateId++;
        if (updateId == 0) updateId++;

        this.world = world;
        this.player = player;
        this.castOrigin = castOrigin;
        this.viewVector = viewVector;
        this.maxCastDistance = maxCastDistance;
        this.hitResult = hit;

        if (this.hitResult.getType() == HitResult.Type.BLOCK) {
            this.pos = ((BlockHitResult) hit).getBlockPos();
            this.blockEntity = this.world.getBlockEntity(this.pos);
            this.entity = null;
            setState(world.getBlockState(pos));
        } else if (this.hitResult.getType() == HitResult.Type.ENTITY) {
            this.entity = ((EntityHitResult) hit).getEntity();
            this.pos = entity.blockPosition();
            this.blockEntity = null;
            setState(Blocks.AIR.defaultBlockState());
        }

        if (viewEntity != null) {
            var px = viewEntity.xo + (viewEntity.getX() - viewEntity.xo) * partialTicks;
            var py = viewEntity.yo + (viewEntity.getY() - viewEntity.yo) * partialTicks;
            var pz = viewEntity.zo + (viewEntity.getZ() - viewEntity.zo) * partialTicks;
            this.renderingVec = new Vec3(this.pos.getX() - px, this.pos.getY() - py, this.pos.getZ() - pz);
            this.partialFrame = partialTicks;
        }
    }

    public void setState(BlockState state) {
        this.state = state;
        this.block = state.getBlock();
        this.stack = block.getCloneItemStack(world, pos, state);
        this.blockRegistryName = BuiltInRegistries.BLOCK.getKey(block);
    }

    public void setDataAccess(boolean dataAccess) {
        this.dataAccess = dataAccess;
    }

    private boolean isTagCorrectBlockEntity() {
        if (blockEntity == null) return false;

        var tag = DataReader.CLIENT.raw();

        if (tag == null) {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }

        var x = tag.getInt("x");
        var y = tag.getInt("y");
        var z = tag.getInt("z");

        var hitPos = ((BlockHitResult) hitResult).getBlockPos();
        if (x == hitPos.getX() && y == hitPos.getY() && z == hitPos.getZ())
            return true;
        else {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }
    }

    private boolean isTagCorrectEntity() {
        if (entity == null) return false;

        var tag = DataReader.CLIENT.raw();

        if (tag == null || !tag.contains("WailaEntityID")) {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }

        var id = tag.getInt("WailaEntityID");

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
