package mcp.mobius.waila.plugin.harvest.provider;

import java.awt.*;
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
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.harvest.component.ToolComponent;
import mcp.mobius.waila.plugin.harvest.config.HarvestDisplayMode;
import mcp.mobius.waila.plugin.harvest.config.Options;
import mcp.mobius.waila.plugin.harvest.tool.ToolTier;
import mcp.mobius.waila.plugin.harvest.tool.ToolType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public enum HarvestProvider implements IBlockComponentProvider, IEventListener {

    INSTANCE;

    private static final ResourceLocation CLASSIC_HARVESTABLE = Options.rl("classic.harvestable");
    private static final ResourceLocation CLASSIC_EFFECTIVE_TOOL = Options.rl("classic.effective_tool");
    private static final ResourceLocation CLASSIC_LEVEL = Options.rl("classic.level");

    private static final ResourceLocation CLASSIC_MINIMAL = Options.rl("classic.minimal");

    private static final ToolType UNBREAKABLE = new ToolType();

    public final Map<BlockState, List<ToolType>> toolsCache = new Reference2ObjectOpenHashMap<>();
    public final Map<BlockState, ToolTier> tierCache = new Reference2ObjectOpenHashMap<>();

    private int updateId = 0;
    private BlockState state;

    private final List<ToolComponent> toolComponents = new ArrayList<>();
    private boolean renderComponents = false;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.ENABLED)) return;
        if (accessor.getPlayer().isCreative() && !config.getBoolean(Options.CREATIVE)) return;

        if (IApiService.INSTANCE.isDevEnv() && config.getBoolean(Options.DEV_DISABLE_CACHE)) {
            toolsCache.clear();
            tierCache.clear();
        }

        updateId = accessor.getUpdateId();
        state = accessor.getBlockState();

        var unbreakable = state.getDestroySpeed(accessor.getWorld(), accessor.getPosition()) < 0;

        var tools = toolsCache.get(state);
        if (tools == null) {
            tools = new ArrayList<>();

            if (unbreakable) {
                tools.add(UNBREAKABLE);
            } else {
                for (var tool : ToolType.all()) {
                    if (tool.blockPredicate.test(state)) {
                        tools.add(tool);
                    }
                }
                if (tools.isEmpty()) tools = List.of();
            }
            toolsCache.put(state, tools);
        }

        var highestTier = tierCache.get(state);
        if (highestTier == null) {
            highestTier = ToolTier.NONE;
            for (var tier : ToolTier.all()) {
                if (state.is(tier.incorrect)) continue;
                highestTier = tier;
                break;
            }
            tierCache.put(state, highestTier);
        }

        HarvestDisplayMode displayMode = config.getEnum(Options.DISPLAY_MODE);
        if (displayMode == HarvestDisplayMode.MODERN) return;

        var heldStack = accessor.getPlayer().getInventory().getSelectedItem();

        if (displayMode == HarvestDisplayMode.CLASSIC) {
            tooltip.setLine(CLASSIC_HARVESTABLE, Component.empty()
                .append(getHarvestableSymbol(accessor, unbreakable))
                .append(" ")
                .append(Component.translatable(Tl.Tooltip.Harvest.HARVESTABLE)));

            if (!tools.isEmpty() && !unbreakable) tooltip.setLine(CLASSIC_EFFECTIVE_TOOL, new PairComponent(
                Component.translatable(Tl.Tooltip.Harvest.EFFECTIVE_TOOL),
                getToolText(tools, heldStack)));

            if (highestTier != ToolTier.NONE) tooltip.setLine(CLASSIC_LEVEL, new PairComponent(
                Component.translatable(Tl.Tooltip.Harvest.LEVEL),
                getTierText(highestTier, heldStack)));
        } else if (displayMode == HarvestDisplayMode.CLASSIC_MINIMAL) {
            var text = Component.empty();
            text.append(getHarvestableSymbol(accessor, unbreakable));

            if (!tools.isEmpty() && !unbreakable) {
                text.append(" | ").append(getToolText(tools, heldStack));
                if (highestTier != ToolTier.NONE) {
                    text.append(" | ");
                }
            }

            if (highestTier != ToolTier.NONE) {
                text.append(getTierText(highestTier, heldStack));
            }

            tooltip.setLine(CLASSIC_MINIMAL, text);
        }
    }

    @Override
    public void onHandleTooltip(ITooltip tooltip, ICommonAccessor accessor, IPluginConfig config) {
        renderComponents = false;

        if (!config.getBoolean(Options.ENABLED)) return;

        HarvestDisplayMode displayMode = config.getEnum(Options.DISPLAY_MODE);
        if (displayMode != HarvestDisplayMode.MODERN) return;

        if (updateId != accessor.getUpdateId()) return;

        var tools = toolsCache.get(state);
        var highestTier = tierCache.get(state);
        if (tools == null || highestTier == null || tools.isEmpty()) return;

        var heldStack = accessor.getPlayer().getInventory().getSelectedItem();

        var line = tooltip.getLine(tooltip.getLineCount() - 1);
        line.with(GrowingComponent.INSTANCE);

        toolComponents.clear();
        for (var tool : tools) {
            ToolComponent component;
            if (tool == UNBREAKABLE) {
                component = new ToolComponent(null, false);
            } else {
                var icon = tool.getIcon(highestTier);
                Boolean matches = null;
                if (state.requiresCorrectToolForDrops()) {
                    matches = tool.itemPredicate.test(heldStack);
                    if (highestTier != ToolTier.NONE) {
                        var heldTier = ToolTier.get(heldStack);
                        matches = matches && heldTier != null && heldTier.isBetterThanOrEqualTo(highestTier);
                    }
                }
                component = new ToolComponent(icon, matches);
            }
            line.with(component);
            toolComponents.add(component);
        }

        renderComponents = true;
    }

    @Override
    public void onAfterTooltipRender(GuiGraphics ctx, Rectangle rect, ICommonAccessor accessor, IPluginConfig config) {
        if (!renderComponents) return;

        for (var component : toolComponents) {
            component.actuallyRender(ctx, rect.y + rect.height - 13);
        }
    }

    @NotNull
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    private MutableComponent getHarvestableSymbol(IBlockAccessor accessor, boolean unbreakable) {
        return unbreakable || !accessor.getPlayer().hasCorrectToolForDrops(state)
            ? Component.literal("\u2718").withStyle(ChatFormatting.RED)
            : Component.literal("\u2714").withStyle(ChatFormatting.GREEN);
    }

    @NotNull
    private static MutableComponent getToolText(List<ToolType> tools, ItemStack heldStack) {
        var toolText = Component.empty();
        var toolIter = tools.iterator();
        while (toolIter.hasNext()) {
            var tool = toolIter.next();
            if (tool == UNBREAKABLE) continue;

            toolText.append(tool.text.copy().withStyle(tool.itemPredicate.test(heldStack) ? ChatFormatting.GREEN : ChatFormatting.RED));
            if (toolIter.hasNext()) toolText.append(", ");
        }
        return toolText;
    }

    @NotNull
    private static MutableComponent getTierText(ToolTier highestTier, ItemStack heldStack) {
        var tierText = I18n.exists(highestTier.tlKey())
            ? Component.translatable(highestTier.tlKey())
            : Component.literal(String.valueOf(highestTier.index));

        var heldTier = ToolTier.get(heldStack);
        tierText.withStyle(heldTier != null && heldTier.isBetterThanOrEqualTo(highestTier) ? ChatFormatting.GREEN : ChatFormatting.RED);

        return tierText;
    }

}
