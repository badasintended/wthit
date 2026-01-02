package mcp.mobius.waila.mixin;

import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LecternBlockEntity.class)
public interface LecternBlockEntityAccess {

    @Accessor("pageCount")
    int wthit_pageCount();

}
