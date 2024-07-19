package mcp.mobius.waila.forge;

import java.util.List;

import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.service.ApiService;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.Nullable;

public class ForgeApiService extends ApiService {

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
