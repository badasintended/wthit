package mcp.mobius.waila.plugin.harvest.service;

import mcp.mobius.waila.api.IToolType;
import mcp.mobius.waila.api.__internal__.IHarvestService;
import mcp.mobius.waila.plugin.harvest.provider.HarvestProvider;
import mcp.mobius.waila.plugin.harvest.tool.ToolTier;
import mcp.mobius.waila.plugin.harvest.tool.ToolType;
import net.minecraft.resources.ResourceLocation;

public class HarvestService implements IHarvestService {

    @Override
    public void addToolType(ResourceLocation id, IToolType toolType) {
        ((ToolType) toolType).bind(id);
    }

    @Override
    public IToolType.Builder0 createToolTypeBuilder() {
        return new ToolType();
    }

    @Override
    public void resetCache() {
        ToolTier.resetMap();
        ToolType.all().forEach(ToolType::resetIcons);

        HarvestProvider.INSTANCE.toolsCache.clear();
        HarvestProvider.INSTANCE.tierCache.clear();
    }

}
