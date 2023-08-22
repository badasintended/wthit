package mcp.mobius.waila.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChiseledBookShelfBlockEntity.class)
public interface ChiseledBookShelfBlockEntityAccess {

    @Accessor("items")
    NonNullList<ItemStack> wthit_items();

}
