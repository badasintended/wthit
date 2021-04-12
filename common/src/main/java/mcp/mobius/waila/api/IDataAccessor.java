package mcp.mobius.waila.api;

import net.minecraft.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * The Accessor is used to get some basic data out of the game without having to request direct access to the game engine.<br>
 * It will also return things that are unmodified by the overriding systems (like getStack).<br>
 * An instance of this interface is passed to most of Waila Block/TileEntity callbacks.
 * TODO: Remove
 */
@Deprecated
public interface IDataAccessor extends IBlockAccessor {

    @Nullable
    @Deprecated
    BlockEntity getTileEntity();

}
