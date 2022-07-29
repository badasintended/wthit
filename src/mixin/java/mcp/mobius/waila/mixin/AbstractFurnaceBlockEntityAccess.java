package mcp.mobius.waila.mixin;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccess {

    @Accessor("cookingProgress")
    int wthit_cookingProgress();

    @Accessor("cookingTotalTime")
    int wthit_cookingTotalTime();

}
