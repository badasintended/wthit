package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IShearable;
import net.minecraft.world.level.block.CherryLeavesBlock;
import net.minecraft.world.level.block.DeadBushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.MangroveLeavesBlock;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WebBlock;
import org.spongepowered.asm.mixin.Mixin;


@Mixin({
    CherryLeavesBlock.class,
    DeadBushBlock.class,
    LeavesBlock.class,
    MangroveLeavesBlock.class,
    SeagrassBlock.class,
    TallGrassBlock.class,
    VineBlock.class,
    WebBlock.class})
public class ShearableBlocksMixin implements IShearable {

}
