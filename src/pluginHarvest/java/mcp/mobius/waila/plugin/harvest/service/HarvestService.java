package mcp.mobius.waila.plugin.harvest.service;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import mcp.mobius.waila.api.IToolType;
import mcp.mobius.waila.api.__internal__.IHarvestService;
import mcp.mobius.waila.plugin.harvest.tool.ToolType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class HarvestService implements IHarvestService {

    private static Function<Tier, @Nullable TagKey<Block>> tierTagKeyFunction = tier -> null;
    private static Supplier<List<Tier>> tierListSupplier = List::of;

    @Nullable
    public static TagKey<Block> getTierTag(Tier tier) {
        return tierTagKeyFunction.apply(tier);
    }

    public static List<Tier> getTiers() {
        return tierListSupplier.get();
    }

    @Override
    public void addToolType(ResourceLocation id, IToolType toolType) {
        ToolType.MAP.put(id, (ToolType) toolType);
    }

    @Override
    public IToolType.Builder0 createToolTypeBuilder() {
        return new ToolType();
    }

}
