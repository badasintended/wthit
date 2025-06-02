package mcp.mobius.waila.access;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IDataReader;
import mcp.mobius.waila.api.IEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
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
import org.jetbrains.annotations.Nullable;

public enum ClientAccessor implements ICommonAccessor, IBlockAccessor, IEntityAccessor {

    INSTANCE;

    private Level world;
    private Player player;
    private HitResult hitResult;
    private Vec3 renderingVec = null;
    private Block block = Blocks.AIR;
    private BlockState state = Blocks.AIR.defaultBlockState();
    private BlockPos pos = BlockPos.ZERO;
    private ResourceLocation blockRegistryName = BuiltInRegistries.ITEM.getDefaultKey();
    private @Nullable BlockEntity blockEntity;
    private @Nullable Entity entity;
    private long timeLastUpdate = System.currentTimeMillis();
    private ItemStack stack = ItemStack.EMPTY;
    private int updateId;
    private boolean dataAccess = true;
    private Vec3 rayCastOrigin;
    private Vec3 rayCastDirection;
    private double rayCastMaxDistance;
    private float frameTime;

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

    public void setEntity(@Nullable Entity entity) {
        this.entity = entity;
    }

    @Override
    public BlockPos getPosition() {
        return this.pos;
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
    public IDataReader getData() {
        if (!dataAccess) return DataReader.NOOP;

        var data = DataReader.CLIENT;
        if (!isTagCorrectBlockEntity() && !isTagCorrectEntity()) data.reset(null);
        return data;
    }

    @Override
    public long getServerDataTime() {
        var data = getData().raw();
        return data.getLong("WailaTime").orElseGet(System::currentTimeMillis);
    }

    @Override
    public @Nullable Direction getSide() {
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

    @Override
    public Vec3 getRayCastOrigin() {
        return rayCastOrigin;
    }

    @Override
    public Vec3 getRayCastDirection() {
        return rayCastDirection;
    }

    @Override
    public double getRayCastMaxDistance() {
        return rayCastMaxDistance;
    }

    @Override
    public float getFrameTime() {
        return frameTime;
    }

    public void set(Level world, Player player, HitResult hit, Entity viewEntity, Vec3 rayCastOrigin, Vec3 rayCastDirection, double rayCastMaxDistance, float frameTime) {
        this.updateId++;
        if (updateId == 0) updateId++;

        this.world = world;
        this.player = player;
        this.hitResult = hit;
        this.rayCastMaxDistance = rayCastMaxDistance;
        this.rayCastOrigin = rayCastOrigin;
        this.rayCastDirection = rayCastDirection;
        this.frameTime = frameTime;

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
            var px = viewEntity.xo + (viewEntity.getX() - viewEntity.xo) * frameTime;
            var py = viewEntity.yo + (viewEntity.getY() - viewEntity.yo) * frameTime;
            var pz = viewEntity.zo + (viewEntity.getZ() - viewEntity.zo) * frameTime;
            this.renderingVec = new Vec3(this.pos.getX() - px, this.pos.getY() - py, this.pos.getZ() - pz);
        }
    }

    public void setState(BlockState state) {
        this.state = state;
        this.block = state.getBlock();
        this.stack = state.getCloneItemStack(world, pos, true);
        this.blockRegistryName = BuiltInRegistries.BLOCK.getKey(block);
    }

    public void setDataAccess(boolean dataAccess) {
        this.dataAccess = dataAccess;
    }

    private boolean isTagCorrectBlockEntity() {
        if (blockEntity == null) return false;

        var tag = DataReader.CLIENT.raw();

        if (tag == null || tag.isEmpty() || !tag.contains("x") || !tag.contains("y") || !tag.contains("z")) {
            this.timeLastUpdate = System.currentTimeMillis() - 250;
            return false;
        }

        var x = tag.getInt("x").orElseThrow();
        var y = tag.getInt("y").orElseThrow();
        var z = tag.getInt("z").orElseThrow();

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

        var id = tag.getInt("WailaEntityID").orElseThrow();

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
