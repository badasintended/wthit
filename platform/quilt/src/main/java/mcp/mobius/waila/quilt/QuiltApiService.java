package mcp.mobius.waila.quilt;

import mcp.mobius.waila.service.ApiService;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class QuiltApiService extends ApiService {

    @Override
    public @Nullable TagKey<Block> getTierTag(Tier tier) {
        return tier.getLevel() == 0 ? null : MiningLevelManager.getBlockTag(tier.getLevel());
    }

}
