package mcp.mobius.waila.hud;

import com.mojang.text2speech.Narrator;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.data.DataAccessor;
import mcp.mobius.waila.util.RaycastUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;
import static mcp.mobius.waila.hud.ComponentHandler.gatherBlock;
import static mcp.mobius.waila.hud.ComponentHandler.gatherEntity;

public class ClientTickHandler {

    protected static Narrator narrator;
    protected static String lastNarration = "";

    private static final Tooltip TOOLTIP = new Tooltip();
    private static final Component SNEAK_DETAIL = new TranslatableComponent("tooltip.waila.sneak_for_details").withStyle(ChatFormatting.ITALIC);

    public static void tick() {
        TooltipRenderer.shouldRender = false;

        Minecraft client = Minecraft.getInstance();
        WailaConfig config = Waila.config.get();

        if (client.level == null
            || !config.getGeneral().isDisplayTooltip()
            || config.getGeneral().getDisplayMode() == IWailaConfig.General.DisplayMode.HOLD_KEY && !WailaClient.showOverlay.isDown()
            || client.screen != null && !(client.screen instanceof ChatScreen)
            || config.getGeneral().isHideFromPlayerList() && client.options.keyPlayerList.consumeClick() && client.getConnection().getOnlinePlayers().size() > 1
            || config.getGeneral().isHideFromDebug() && client.options.renderDebug) {
            return;
        }

        HitResult target = RaycastUtil.fire();
        if (target.getType() == HitResult.Type.MISS) {
            return;
        }

        Player player = client.player;
        DataAccessor accessor = DataAccessor.INSTANCE;
        accessor.set(client.level, player, target, client.cameraEntity, client.getFrameTime());

        TooltipRenderer.beginBuild();

        if (target.getType() == HitResult.Type.BLOCK) {
            Block block = accessor.getBlock();
            if (block instanceof LiquidBlock && !PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_FLUID)
                || !PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_BLOCK)
                || Waila.blockBlacklist.contains(block)) {
                return;
            }

            BlockState state = ComponentHandler.getOverrideBlock(target);
            accessor.setState(state);

            TOOLTIP.clear();
            gatherBlock(accessor, TOOLTIP, HEAD);
            TooltipRenderer.addLines(TOOLTIP);

            TOOLTIP.clear();
            gatherBlock(accessor, TOOLTIP, BODY);
            if (Waila.config.get().getGeneral().isShiftForDetails() && !TOOLTIP.isEmpty() && !player.isShiftKeyDown()) {
                TooltipRenderer.addLine(SNEAK_DETAIL);
            } else {
                TooltipRenderer.addLines(TOOLTIP);
            }

            TOOLTIP.clear();
            gatherBlock(accessor, TOOLTIP, TAIL);
            TooltipRenderer.addLines(TOOLTIP);
        } else if (target.getType() == HitResult.Type.ENTITY) {
            if (!PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY)
                || Waila.entityBlacklist.contains(accessor.getEntity().getType())) {
                return;
            }

            Entity targetEnt = ComponentHandler.getOverrideEntity(target);
            accessor.setEntity(targetEnt);

            if (targetEnt != null) {
                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, HEAD);
                TooltipRenderer.addLines(TOOLTIP);

                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, BODY);
                if (Waila.config.get().getGeneral().isShiftForDetails() && !TOOLTIP.isEmpty() && !player.isShiftKeyDown()) {
                    TooltipRenderer.addLine(SNEAK_DETAIL);
                } else {
                    TooltipRenderer.addLines(TOOLTIP);
                }

                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, TAIL);
                TooltipRenderer.addLines(TOOLTIP);
            }
        }

        if (PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_ITEM)) {
            TooltipRenderer.setStack(ComponentHandler.getDisplayItem(target));
        }

        TooltipRenderer.endBuild();
        TooltipRenderer.shouldRender = true;
    }

    protected static Narrator getNarrator() {
        return narrator == null ? narrator = Narrator.getNarrator() : narrator;
    }

}
