package mcp.mobius.waila.mixin;

import java.util.Optional;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChiseledBookShelfBlock.class)
public interface ChiseledBookShelfBlockAccess {

    @Invoker("getRelativeHitCoordinatesForBlockFace")
    static Optional<Vec2> wthit_getRelativeHitCoordinatesForBlockFace(BlockHitResult $$0, Direction $$1) {
        throw new AssertionError("mixin");
    }

    @Invoker("getHitSlot")
    static int wthit_getHitSlot(Vec2 $$0) {
        throw new AssertionError("mixin");
    }

}
