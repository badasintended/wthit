package mcp.mobius.waila.mixin;

import java.util.OptionalInt;

import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChiseledBookShelfBlock.class)
public interface ChiseledBookShelfBlockAccess {

    @Invoker("getHitSlot")
    OptionalInt wthit_getHitSlot(BlockHitResult $$0, BlockState $$1);

}
