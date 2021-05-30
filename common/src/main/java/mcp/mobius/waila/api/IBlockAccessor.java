package mcp.mobius.waila.api;

import mcp.mobius.waila.api.internal.ApiSide;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiSide.ClientOnly
@ApiStatus.NonExtendable
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

    NbtCompound getServerData();

    double getPartialFrame();

    Direction getSide();

    ItemStack getStack();

    String getModNameFormat();

    String getBlockNameFormat();

    String getFluidNameFormat();

    String getRegistryNameFormat();

}
