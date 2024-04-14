package mcp.mobius.waila.plugin.harvest.tool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.api.__internal__.Internals;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public final class ToolTier {

    public static final ToolTier NONE = Internals.unsafeAlloc(ToolTier.class);

    private static final Supplier<Map<Tier, ToolTier>> TIERS = Suppliers.memoize(() -> {
        var builder = ImmutableMap.<Tier, ToolTier>builder();
        var tiers = IApiService.INSTANCE.getTiers();
        for (var i = 0; i < tiers.size(); i++) {
            var tier = tiers.get(i);
            builder.put(tier, new ToolTier(tier, i));
        }
        return builder.build();
    });

    private static final Supplier<Map<ResourceLocation, String>> VANILLA_TIER_TL_KEYS = Suppliers.memoize(() -> {
        var map = new HashMap<ResourceLocation, String>();
        for (var tier : Tiers.values()) {
            map.put(tier.getIncorrectBlocksForDrops().location(), tier.name().toLowerCase(Locale.ROOT));
        }
        return map;
    });

    public final Tier tier;
    public final int index;
    public final @Nullable TagKey<Block> incorrect;

    private final Supplier<String> tlKey;

    public ToolTier(Tier tier, int index) {
        this.tier = tier;
        this.index = index;
        this.incorrect = tier.getIncorrectBlocksForDrops();

        this.tlKey = Suppliers.memoize(() -> {
            var vanilla = VANILLA_TIER_TL_KEYS.get().get(incorrect.location());
            var key = vanilla != null ? vanilla : incorrect.location().toLanguageKey();

            return Tl.Tooltip.Harvest.TIER + "." + key;
        });
    }

    public static Collection<ToolTier> all() {
        return TIERS.get().values();
    }

    @Nullable
    public static ToolTier get(Tier tier) {
        return TIERS.get().get(tier);
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
