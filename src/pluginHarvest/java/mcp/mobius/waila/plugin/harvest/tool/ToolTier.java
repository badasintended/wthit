package mcp.mobius.waila.plugin.harvest.tool;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.world.item.Tier;

public record ToolTier(
    Tier tier,
    int index
) {

    public static final ToolTier NONE = new ToolTier(null, -1);

    private static final Supplier<Map<Tier, ToolTier>> TIERS = Suppliers.memoize(() -> {
        var builder = ImmutableMap.<Tier, ToolTier>builder();
        var tiers = IApiService.INSTANCE.getTiers();
        for (var i = 0; i < tiers.size(); i++) {
            var tier = tiers.get(i);
            builder.put(tier, new ToolTier(tier, i));
        }
        return builder.build();
    });

    public static Collection<ToolTier> all() {
        return TIERS.get().values();
    }

    public static ToolTier get(Tier tier) {
        return TIERS.get().get(tier);
    }

}
