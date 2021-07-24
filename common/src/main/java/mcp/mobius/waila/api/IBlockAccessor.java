package mcp.mobius.waila.api;

import mcp.mobius.waila.api.internal.ApiSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * The Accessor is used to get some basic data out of the game without having to request direct access to the game engine.
 * <p>
 * It will also return things that are unmodified by the overriding systems (like getStack).
 */
@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IBlockAccessor {

    Level getWorld();

    Player getPlayer();

    Block getBlock();

    BlockState getBlockState();

    @Nullable
    BlockEntity getBlockEntity();

    HitResult getHitResult();

    BlockPos getPosition();

    @Nullable
    Vec3 getRenderingPosition();

    CompoundTag getServerData();

    double getPartialFrame();

    Direction getSide();

    ItemStack getStack();

    String getModNameFormat();

    String getBlockNameFormat();

    String getFluidNameFormat();

    String getRegistryNameFormat();

}
