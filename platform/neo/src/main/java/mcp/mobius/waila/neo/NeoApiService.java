package mcp.mobius.waila.neo;

import java.util.List;

import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.service.ApiService;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.TierSortingRegistry;
import org.jetbrains.annotations.Nullable;

public class NeoApiService extends ApiService {

    @Override
    @SuppressWarnings("DataFlowIssue")
    public IModInfo getModInfo(ItemStack stack) {
        return ModInfo.get(stack.getItem().getCreatorModId(stack));
    }

    @Override
    public String getDefaultEnergyUnit() {
        return "FE";
    }

    @Override
    public @Nullable TagKey<Block> getTierTag(Tier tier) {
        return tier.getTag();
    }

    @Override
    public List<Tier> getTiers() {
        return TierSortingRegistry.getSortedTiers();
    }

}
