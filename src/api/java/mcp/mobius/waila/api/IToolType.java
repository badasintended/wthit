package mcp.mobius.waila.api;

import java.util.function.Predicate;

import mcp.mobius.waila.api.__internal__.IHarvestService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
@ApiStatus.Experimental
public interface IToolType {

    static Builder0 builder() {
        return IHarvestService.INSTANCE.createToolTypeBuilder();
    }

    interface Builder0 {

        /**
         * The wooden stack, or any stack of the type that has its {@linkplain ItemStack#getDestroySpeed(BlockState) destroy speed}
         * the same as or higher than {@linkplain ToolMaterial#WOOD wood}.
         */
        Builder1 lowestTierStack(ItemStack stack);

        /**
         * The wooden item, or any item of the type that has its {@linkplain Item#getDestroySpeed(ItemStack, BlockState) destroy speed}
         * the same as or higher than {@linkplain ToolMaterial#WOOD wood}
         */
        Builder1 lowestTierItem(ItemLike item);

    }

    interface Builder1 {

        /**
         * The block state predicate, used to check if the current block can be mined by the tool.
         */
        Builder2 blockPredicate(Predicate<BlockState> predicate);

        /**
         * The block tag, used to check if the current block can be mined by the tool.
         */
        Builder2 blockTag(TagKey<Block> tag);

        Builder2 blockTag(ResourceLocation tag);

    }

    interface Builder2 {

        /**
         * The stack predicate, used to check if the current stack is valid for the tool type.
         */
        Builder3 itemPredicate(Predicate<ItemStack> predicate);

        /**
         * The item tag, used to check if the current stack is valid for the tool type.
         */
        Builder3 itemTag(TagKey<Item> tag);

        Builder3 itemTag(ResourceLocation tag);

    }

    interface Builder3 {

        IToolType build();

    }

}
