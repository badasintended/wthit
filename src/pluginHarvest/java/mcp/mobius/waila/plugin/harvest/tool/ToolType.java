package mcp.mobius.waila.plugin.harvest.tool;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import mcp.mobius.waila.api.IToolType;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ToolType implements IToolType, IToolType.Builder0, IToolType.Builder1, IToolType.Builder2, IToolType.Builder3 {

    private static final Map<ResourceLocation, ToolType> MAP = new LinkedHashMap<>();

    public ResourceLocation id;
    public ItemStack lowestTierStack;
    public Predicate<BlockState> blockPredicate;
    public Predicate<ItemStack> itemPredicate;
    public Component text;

    private Supplier<Map<ToolTier, ItemStack>> icons;

    public ToolType() {
        resetIcons();
    }

    public void resetIcons() {
        icons = Suppliers.memoize(() -> {
            var tiers = ToolTier.all();
            var map = new Reference2ObjectOpenHashMap<ToolTier, ItemStack>();

            for (var item : BuiltInRegistries.ITEM) {
                var stack = item.getDefaultInstance();
                if (itemPredicate.test(stack)) {
                    for (var registeredTier : tiers) {
                        if (!map.containsKey(registeredTier)) {
                            var itemTier = ToolTier.get(stack);
                            if (itemTier != null && itemTier.isEqualTo(registeredTier)) {
                                map.put(registeredTier, stack);
                            }
                        }
                    }
                }
            }

            if (map.size() < tiers.size()) {
                for (var tier : tiers) {
                    map.putIfAbsent(tier, lowestTierStack);
                }
            }

            return map;
        });
    }

    public ItemStack getIcon(ToolTier tier) {
        if (tier == ToolTier.NONE) return lowestTierStack;
        else return icons.get().get(tier);
    }

    public void bind(ResourceLocation id) {
        this.id = id;
        this.text = Component.translatable(Tl.Tooltip.Harvest.TOOL + "." + id.toLanguageKey());

        MAP.put(id, this);
    }

    public static Collection<ToolType> all() {
        return MAP.values();
    }

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
