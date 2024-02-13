package mcp.mobius.waila.plugin.harvest.tool;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.api.__internal__.Internals;
import mcp.mobius.waila.buildconst.Tl;
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

    public final Tier tier;
    public final int index;
    public final @Nullable TagKey<Block> tag;
    public final String tlKey;

    public ToolTier(Tier tier, int index) {
        this.tier = tier;
        this.index = index;
        this.tag = IApiService.INSTANCE.getTierTag(tier);

        if (tier instanceof Tiers vanilla)
            this.tlKey = Tl.Tooltip.Harvest.TIER + "." + vanilla.name().toLowerCase(Locale.ROOT);
        else if (tag != null)
            this.tlKey = Tl.Tooltip.Harvest.TIER + "." + tag.location().toLanguageKey();
        else
            this.tlKey = Tl.Tooltip.Harvest.TIER + "." + tier.getLevel();
    }

    public static Collection<ToolTier> all() {
        return TIERS.get().values();
    }

    public static ToolTier get(Tier tier) {
        return TIERS.get().get(tier);
    }

}
