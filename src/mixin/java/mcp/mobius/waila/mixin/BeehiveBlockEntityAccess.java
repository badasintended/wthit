package mcp.mobius.waila.mixin;

import java.util.List;

import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeehiveBlockEntity.class)
public interface BeehiveBlockEntityAccess {

    @Accessor("stored")
    List<BeehiveBlockEntity$BeeDataAccess> wthit_stored();

}
