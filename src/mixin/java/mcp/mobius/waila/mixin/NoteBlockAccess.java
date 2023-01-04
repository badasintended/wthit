package mcp.mobius.waila.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NoteBlock.class)
public interface NoteBlockAccess {

    @Invoker("getCustomSoundId")
    ResourceLocation wthit_getCustomSoundId(Level world, BlockPos pos);

}
