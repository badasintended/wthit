package mcp.mobius.waila.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface IBlockAccessor {

    World getWorld();

    PlayerEntity getPlayer();

    Block getBlock();

    BlockState getBlockState();

    @Nullable
    BlockEntity getBlockEntity();

    HitResult getHitResult();

    BlockPos getPosition();

    @Nullable
    Vec3d getRenderingPosition();

    CompoundTag getServerData();

    double getPartialFrame();

    Direction getSide();

    ItemStack getStack();

}
