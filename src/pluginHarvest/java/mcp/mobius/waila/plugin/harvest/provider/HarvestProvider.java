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
import mcp.mobius.waila.plugin.harvest.tool.ToolType;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;

public enum HarvestProvider implements IBlockComponentProvider, IEventListener {

    INSTANCE;

    public final Map<BlockState, List<ToolType>> toolsCache = new Reference2ObjectOpenHashMap<>();
    public final Map<BlockState, Tier> tierCache = new Reference2ObjectOpenHashMap<>();

    private int updateId = 0;
    private BlockState state;
    private List<ToolType> tools;
    private Tier highestTier;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        updateId = accessor.getUpdateId();
        state = accessor.getBlockState();

        var destroySpeed = state.getDestroySpeed(accessor.getWorld(), accessor.getPosition());
        if (destroySpeed <= 0) return;

        tools = toolsCache.get(state);
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

        highestTier = tierCache.get(state);
        if (highestTier == null) {
            highestTier = Tiers.WOOD;
            for (var tier : IApiService.INSTANCE.getTiers()) {
                var tag = IApiService.INSTANCE.getTierTag(tier);
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
        if (tools.isEmpty()) return;

        var heldStack = accessor.getPlayer().getInventory().getSelected();

        var line = tooltip.getLine(tooltip.getLineCount() - 1);
        line.with(GrowingComponent.INSTANCE);

        for (var tool : tools) {
            var icon = tool.icons.get().get(highestTier);
            Boolean matches = null;
            if (state.requiresCorrectToolForDrops()) {
                matches = tool.itemPredicate.test(heldStack);
                if (highestTier != Tiers.WOOD && heldStack.getItem() instanceof TieredItem tiered) {
                    matches = matches && tiered.getTier() == highestTier;
                }
            }
            line.with(new ToolComponent(icon, matches));
        }
    }

}
