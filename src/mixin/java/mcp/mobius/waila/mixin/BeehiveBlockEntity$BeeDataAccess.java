package mcp.mobius.waila.mixin;

import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.world.level.block.entity.BeehiveBlockEntity$BeeData")
public interface BeehiveBlockEntity$BeeDataAccess {

    @Accessor("occupant")
    BeehiveBlockEntity.Occupant wthit_occupant();

}
