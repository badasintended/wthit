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
import mcp.mobius.waila.api.component.GrowingComponent;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.harvest.component.ToolComponent;
import mcp.mobius.waila.plugin.harvest.config.HarvestDisplayMode;
import mcp.mobius.waila.plugin.harvest.config.Options;
import mcp.mobius.waila.plugin.harvest.tool.ToolTier;
import mcp.mobius.waila.plugin.harvest.tool.ToolType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public enum HarvestProvider implements IBlockComponentProvider, IEventListener {

    INSTANCE;

    public final Map<BlockState, List<ToolType>> toolsCache = new Reference2ObjectOpenHashMap<>();
    public final Map<BlockState, ToolTier> tierCache = new Reference2ObjectOpenHashMap<>();

    private int updateId = 0;
    private BlockState state;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.ENABLED)) return;

        updateId = accessor.getUpdateId();
        state = accessor.getBlockState();

        var tools = toolsCache.get(state);
        if (tools == null) {
            tools = new ArrayList<>();
            for (var tool : ToolType.all()) {
                if (tool.blockPredicate.test(state)) {
                    tools.add(tool);
                }
            }
            if (tools.isEmpty()) tools = List.of();
            toolsCache.put(state, tools);
        }

        var highestTier = tierCache.get(state);
        if (highestTier == null) {
            highestTier = ToolTier.NONE;
            for (var tier : ToolTier.all()) {
                if (tier.tag != null && state.is(tier.tag)) {
                    highestTier = tier;
                }
            }
            tierCache.put(state, highestTier);
        }

        HarvestDisplayMode displayMode = config.getEnum(Options.DISPLAY_MODE);
        if (displayMode == HarvestDisplayMode.MODERN) return;

        var heldStack = accessor.getPlayer().getInventory().getSelected();

        if (displayMode == HarvestDisplayMode.CLASSIC) {
            tooltip.addLine(Component.empty().append(getHarvestableSymbol(accessor)).append(" ").append(Component.translatable(Tl.Tooltip.Harvest.HARVESTABLE)));

            if (!tools.isEmpty()) tooltip.addLine(new PairComponent(
                Component.translatable(Tl.Tooltip.Harvest.EFFECTIVE_TOOL),
                getToolText(tools, heldStack)));

            if (highestTier != ToolTier.NONE) tooltip.addLine(new PairComponent(
                Component.translatable(Tl.Tooltip.Harvest.LEVEL),
                getTierText(highestTier, heldStack)));
        } else if (displayMode == HarvestDisplayMode.CLASSIC_MINIMAL) {
            var text = Component.empty();
            text.append(getHarvestableSymbol(accessor));

            if (!tools.isEmpty()) {
                text.append(" | ").append(getToolText(tools, heldStack));
                if (highestTier != ToolTier.NONE) {
                    text.append(" | ");
                }
            }

            if (highestTier != ToolTier.NONE) {
                text.append(getTierText(highestTier, heldStack));
            }

            tooltip.addLine(text);
        }
    }

    @Override
    public void onHandleTooltip(ITooltip tooltip, ICommonAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.ENABLED)) return;

        HarvestDisplayMode displayMode = config.getEnum(Options.DISPLAY_MODE);
        if (displayMode != HarvestDisplayMode.MODERN) return;

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
                    var heldTier = ToolTier.get(tiered.getTier());
                    matches = matches && heldTier.index >= highestTier.index;
                }
            }
            line.with(new ToolComponent(icon, matches));
        }
    }

    @NotNull
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    private MutableComponent getHarvestableSymbol(IBlockAccessor accessor) {
        return accessor.getPlayer().hasCorrectToolForDrops(state) && state.getDestroySpeed(accessor.getWorld(), accessor.getPosition()) > 0
            ? Component.literal("\u2714").withStyle(ChatFormatting.GREEN)
            : Component.literal("\u2718").withStyle(ChatFormatting.RED);
    }

    @NotNull
    private static MutableComponent getToolText(List<ToolType> tools, ItemStack heldStack) {
        var toolText = Component.empty();
        var toolIter = tools.iterator();
        while (toolIter.hasNext()) {
            var tool = toolIter.next();
            toolText.append(tool.text.copy().withStyle(tool.itemPredicate.test(heldStack) ? ChatFormatting.GREEN : ChatFormatting.RED));
            if (toolIter.hasNext()) toolText.append(", ");
        }
        return toolText;
    }

    @NotNull
    private static MutableComponent getTierText(ToolTier highestTier, ItemStack heldStack) {
        var tierText = I18n.exists(highestTier.tlKey)
            ? Component.translatable(highestTier.tlKey)
            : Component.literal(String.valueOf(highestTier.index));
        if (heldStack.getItem() instanceof TieredItem tiered) {
            var heldTier = ToolTier.get(tiered.getTier());
            tierText.withStyle(heldTier.index >= highestTier.index ? ChatFormatting.GREEN : ChatFormatting.RED);
        } else {
            tierText.withStyle(ChatFormatting.RED);
        }
        return tierText;
    }

}
