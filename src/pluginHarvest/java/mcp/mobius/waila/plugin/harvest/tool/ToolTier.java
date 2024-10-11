package mcp.mobius.waila.plugin.harvest.tool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.api.__internal__.Internals;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public final class ToolTier {

    public static final ToolTier NONE = Internals.unsafeAlloc(ToolTier.class);

    private static final Supplier<Map<ResourceLocation, String>> VANILLA_TIER_TL_KEYS = Suppliers.memoize(() -> {
        var map = new HashMap<ResourceLocation, String>();
        // TODO
//        for (var tier : Tiers.values()) {
//            map.put(tier.getIncorrectBlocksForDrops().location(), tier.name().toLowerCase(Locale.ROOT));
//        }
        return map;
    });

    private static Supplier<Map<ResourceLocation, ToolTier>> tiers;

    public final ToolMaterial tier;
    public final int index;
    public final TagKey<Block> incorrect;

    private final Supplier<String> tlKey;

    public ToolTier(ToolMaterial tier, int index) {
        this.tier = tier;
        this.index = index;
        this.incorrect = tier.incorrectBlocksForDrops();

        this.tlKey = Suppliers.memoize(() -> {
            var vanilla = VANILLA_TIER_TL_KEYS.get().get(incorrect.location());
            var key = vanilla != null ? vanilla : incorrect.location().toLanguageKey();

            return Tl.Tooltip.Harvest.TIER + "." + key;
        });
    }

    static {
        resetMap();
    }

    public static void resetMap() {
        tiers = Suppliers.memoize(() -> {
            var builder = ImmutableMap.<ResourceLocation, ToolTier>builder();
            var tiers = IApiService.INSTANCE.getTiers();
            for (var i = 0; i < tiers.size(); i++) {
                var tier = tiers.get(i);
                builder.put(tier.incorrectBlocksForDrops().location(), new ToolTier(tier, i));
            }
            return builder.build();
        });
    }

    public static Collection<ToolTier> all() {
        return tiers.get().values();
    }

    @Nullable
    public static ToolTier get(ItemStack stack) {
        var stackTool = stack.get(DataComponents.TOOL);
        if (stackTool == null) return null;

        for (var toolRule : stackTool.rules()) {
            var correctForDrops = toolRule.correctForDrops().orElse(null);
            if (correctForDrops == Boolean.FALSE && toolRule.blocks() instanceof HolderSet.Named<Block> named) {
                return tiers.get().get(named.key().location());
            }
        }

        return null;
    }

    public String tlKey() {
        return tlKey.get();
    }

    public boolean isEqualTo(ToolTier other) {
        if (this == other) return true;
        if (this.tier == other.tier) return true;
        if (this.incorrect != null && other.incorrect != null) return this.incorrect.location().equals(other.incorrect.location());
        return false;
    }

    public boolean isBetterThanOrEqualTo(ToolTier other) {
        return isEqualTo(other) || this.index >= other.index;
    }

}
