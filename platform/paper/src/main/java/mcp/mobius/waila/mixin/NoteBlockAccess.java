package mcp.mobius.waila.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * Stub interface for Paper. On Fabric/Forge, this is a mixin accessor.
 */
public interface NoteBlockAccess {

    @Nullable Identifier wthit_getCustomSoundId(Level world, BlockPos pos);

}
