package mcp.mobius.waila.mixin;

import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.world.level.block.entity.BeehiveBlockEntity$BeeData")
public interface BeehiveBlockEntity$BeeDataAccess {

    @Accessor("entityData")
    CompoundTag wthit_entityData();

}
