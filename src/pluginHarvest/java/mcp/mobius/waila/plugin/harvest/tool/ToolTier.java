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
            var tag = IApiService.INSTANCE.getTierTag(tier);
            if (tag == null) continue;
            map.put(tag.location(), tier.name().toLowerCase(Locale.ROOT));
        }
        return map;
    });

    public final Tier tier;
    public final int index;
    public final @Nullable TagKey<Block> tag;

    private final Supplier<String> tlKey;

    public ToolTier(Tier tier, int index) {
        this.tier = tier;
        this.index = index;
        this.tag = IApiService.INSTANCE.getTierTag(tier);

        this.tlKey = Suppliers.memoize(() -> {
            String key;

            if (tag != null) {
                var vanilla = VANILLA_TIER_TL_KEYS.get().get(tag.location());
                key = vanilla != null ? vanilla : tag.location().toLanguageKey();
            } else {
                key = String.valueOf(tier.getLevel());
            }

            return Tl.Tooltip.Harvest.TIER + "." + key;
        });
    }

    public static Collection<ToolTier> all() {
        return TIERS.get().values();
    }

    public static ToolTier get(Tier tier) {
        return TIERS.get().get(tier);
    }

    public String tlKey() {
        return tlKey.get();
    }

    public boolean isEqualTo(ToolTier other) {
        if (this == other) return true;
        if (this.tier == other.tier) return true;
        if (this.tag != null && other.tag != null) return this.tag.location().equals(other.tag.location());
        return false;
    }

    public boolean isBetterThanOrEqualTo(ToolTier other) {
        return isEqualTo(other) || this.index >= other.index;
    }

}
