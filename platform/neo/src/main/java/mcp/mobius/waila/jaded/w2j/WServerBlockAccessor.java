package mcp.mobius.waila.jaded.w2j;

import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IServerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import snownee.jade.api.BlockAccessor;

public class WServerBlockAccessor implements BlockAccessor {

    private final IDataWriter data;
    private final IServerAccessor<BlockEntity> accessor;

    public WServerBlockAccessor(IDataWriter data, IServerAccessor<BlockEntity> accessor) {
        this.data = data;
        this.accessor = accessor;
    }

    @Override
    public Block getBlock() {
        return accessor.getTarget().getBlockState().getBlock();
    }

    @Override
    public BlockState getBlockState() {
        return accessor.getTarget().getBlockState();
    }

    @Override
    public BlockEntity getBlockEntity() {
        return accessor.getTarget();
    }

    @Override
    public BlockPos getPosition() {
        return accessor.getTarget().getBlockPos();
    }

    @Override
    public Direction getSide() {
        return accessor.<BlockHitResult>getHitResult().getDirection();
    }

    @Override
    public boolean isFakeBlock() {
        return false;
    }

    @Override
    public ItemStack getFakeBlock() {
        return ItemStack.EMPTY;
    }

    @Override
    public Level getLevel() {
        return accessor.getWorld();
    }

    @Override
    public Player getPlayer() {
        return accessor.getPlayer();
    }

    @Override
    public @NotNull CompoundTag getServerData() {
        return data.raw();
    }

    @Override
    public BlockHitResult getHitResult() {
        return accessor.getHitResult();
    }

    @Override
    public boolean isServerConnected() {
        return true;
    }

    @Override
    public ItemStack getPickedResult() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showDetails() {
        return true;
    }

    @Override
    public Object getTarget() {
        return accessor.getTarget();
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public boolean verifyData(CompoundTag compoundTag) {
        return true;
    }

}
