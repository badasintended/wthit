package mcp.mobius.waila.plugin.harvest.provider;

import java.util.ArrayList;
import java.util.Map;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.plugin.harvest.tool.ToolType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public enum HarvestProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        var state = accessor.getBlockState();
        var world = accessor.getWorld();
        var pos = accessor.getPosition();
        var stack = accessor.getStack();
        var player = accessor.getPlayer();

        var destroySpeed = state.getDestroySpeed(world, pos);
        if (destroySpeed <= 0) return;

        var tools = new ArrayList<Map.Entry<ResourceLocation, ToolType>>();
        for (var entry : ToolType.MAP.entrySet()) {
            var tool = entry.getValue();
            if (tool.blockPredicate.test(state) || tool.lowestTierStack.getDestroySpeed(state) >= Tiers.WOOD.getSpeed()) {
                tools.add(entry);
            }
        }

        if (tools.isEmpty()) return;

        Tier highestTier = Tiers.WOOD;
        for (var tier : IApiService.INSTANCE.getTiers()) {
            var tag = IApiService.INSTANCE.getTierTag(tier);
            if (tag != null && state.is(tag)) {
                highestTier = tier;
            }
        }

        var line = tooltip.addLine();
        for (var tool : tools) {
            var icon = tool.getValue().icons.get().get(highestTier);
            line.with(new ItemComponent(icon));

            if (state.requiresCorrectToolForDrops()) {
                line.with(Component.literal(String.valueOf(player.hasCorrectToolForDrops(state))));
            }
        }
    }

}
