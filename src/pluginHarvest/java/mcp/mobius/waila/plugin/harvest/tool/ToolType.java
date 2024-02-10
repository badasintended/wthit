package mcp.mobius.waila.plugin.harvest.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import mcp.mobius.waila.api.IToolType;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ToolType implements IToolType, IToolType.Builder0, IToolType.Builder1, IToolType.Builder2, IToolType.Builder3 {

    public static final Map<ResourceLocation, ToolType> MAP = new HashMap<>();

    public ResourceLocation id;
    public ItemStack lowestTierStack;
    public Predicate<BlockState> blockPredicate;
    public Predicate<ItemStack> itemPredicate;

    public final Supplier<Map<Tier, ItemStack>> icons = Suppliers.memoize(() -> {
        var tiers = IApiService.INSTANCE.getTiers();
        var map = new HashMap<Tier, ItemStack>();

        for (var item : BuiltInRegistries.ITEM) {
            var stack = item.getDefaultInstance();
            if (itemPredicate.test(stack)) {
                for (var tier : tiers) {
                    if (!map.containsKey(tier) && item instanceof TieredItem tiered && tiered.getTier() == tier) {
                        map.put(tier, stack);
                    }
                }
            }
        }

        if (map.size() < tiers.size()) {
            for (var tier : tiers) {
                map.putIfAbsent(tier, lowestTierStack);
            }
        }

        return ImmutableMap.copyOf(map);
    });

    @Override
    public Builder1 lowestTierStack(ItemStack stack) {
        lowestTierStack = stack;
        return this;
    }

    @Override
    public Builder1 lowestTierItem(ItemLike item) {
        lowestTierStack = new ItemStack(item);
        return this;
    }

    @Override
    public Builder2 blockPredicate(Predicate<BlockState> predicate) {
        blockPredicate = predicate;
        return this;
    }

    @Override
    public Builder2 blockTag(TagKey<Block> tag) {
        blockPredicate = state -> state.is(tag);
        return this;
    }

    @Override
    public Builder2 blockTag(ResourceLocation tag) {
        return blockTag(TagKey.create(Registries.BLOCK, tag));
    }

    @Override
    public Builder3 itemPredicate(Predicate<ItemStack> predicate) {
        itemPredicate = predicate;
        return this;
    }

    @Override
    public Builder3 itemTag(TagKey<Item> tag) {
        itemPredicate = stack -> stack.is(tag);
        return this;
    }

    @Override
    public Builder3 itemTag(ResourceLocation tag) {
        return itemTag(TagKey.create(Registries.ITEM, tag));
    }

    @Override
    public IToolType build() {
        return this;
    }

}
