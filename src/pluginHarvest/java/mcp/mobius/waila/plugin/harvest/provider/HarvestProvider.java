package mcp.mobius.waila.plugin.harvest.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.api.component.GrowingComponent;
import mcp.mobius.waila.plugin.harvest.component.ToolComponent;
import mcp.mobius.waila.plugin.harvest.tool.ToolTier;
import mcp.mobius.waila.plugin.harvest.tool.ToolType;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.state.BlockState;

public enum HarvestProvider implements IBlockComponentProvider, IEventListener {

    INSTANCE;

    public final Map<BlockState, List<ToolType>> toolsCache = new Reference2ObjectOpenHashMap<>();
    public final Map<BlockState, ToolTier> tierCache = new Reference2ObjectOpenHashMap<>();

    private int updateId = 0;
    private BlockState state;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        updateId = accessor.getUpdateId();
        state = accessor.getBlockState();

        var tools = toolsCache.get(state);
        if (tools == null) {
            tools = new ArrayList<>();
            for (var entry : ToolType.MAP.entrySet()) {
                var tool = entry.getValue();
                if (tool.blockPredicate.test(state)) {
                    tools.add(tool);
                }
            }
            if (tools.isEmpty()) tools = List.of();
            toolsCache.put(state, tools);
        }

        if (tools.isEmpty()) return;

        var highestTier = tierCache.get(state);
        if (highestTier == null) {
            highestTier = ToolTier.NONE;
            for (var tier : ToolTier.all()) {
                var tag = IApiService.INSTANCE.getTierTag(tier.tier());
                if (tag != null && state.is(tag)) {
                    highestTier = tier;
                }
            }
            tierCache.put(state, highestTier);
        }
    }

    @Override
    public void onHandleTooltip(ITooltip tooltip, ICommonAccessor accessor, IPluginConfig config) {
        if (updateId != accessor.getUpdateId()) return;

        var tools = toolsCache.get(state);
        var highestTier = tierCache.get(state);
        if (tools == null || highestTier == null || tools.isEmpty()) return;

        var heldStack = accessor.getPlayer().getInventory().getSelected();

        var line = tooltip.getLine(tooltip.getLineCount() - 1);
        line.with(GrowingComponent.INSTANCE);

        for (var tool : tools) {
            var icon = tool.getIcon(highestTier);
            Boolean matches = null;
            if (state.requiresCorrectToolForDrops()) {
                matches = tool.itemPredicate.test(heldStack);
                if (highestTier != ToolTier.NONE && heldStack.getItem() instanceof TieredItem tiered) {
                    matches = matches && ToolTier.get(tiered.getTier()).index() >= highestTier.index();
                }
            }
            line.with(new ToolComponent(icon, matches));
        }
    }

}
