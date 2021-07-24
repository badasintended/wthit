package mcp.mobius.waila.hud;

import java.util.List;

import com.mojang.text2speech.Narrator;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.data.DataAccessor;
import mcp.mobius.waila.util.RaycastUtil;
import mcp.mobius.waila.util.TaggableList;
import mcp.mobius.waila.util.TaggedText;
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
import static mcp.mobius.waila.hud.HudProviderHandler.gatherBlock;
import static mcp.mobius.waila.hud.HudProviderHandler.gatherEntity;

public class HudTickHandler {

    protected static Narrator narrator;
    protected static String lastNarration = "";

    private static final List<Component> TIP = new TaggableList<>(TaggedText::new);
    private static final Component SNEAK_DETAIL = new TranslatableComponent("tooltip.waila.sneak_for_details").withStyle(ChatFormatting.ITALIC);

    public static void tickClient() {
        HudRenderer.shouldRender = false;

        Minecraft client = Minecraft.getInstance();
        WailaConfig config = Waila.config.get();

        if (client.level == null
            || !config.getGeneral().shouldDisplayTooltip()
            || config.getGeneral().getDisplayMode() == WailaConfig.General.DisplayMode.HOLD_KEY && !WailaClient.showOverlay.isDown()
            || client.screen != null && !(client.screen instanceof ChatScreen)
            || config.getGeneral().shouldHideFromPlayerList() && client.options.keyPlayerList.consumeClick() && client.getConnection().getOnlinePlayers().size() > 1
            || config.getGeneral().shouldHideFromDebug() && client.options.renderDebug) {
            return;
        }

        HitResult target = RaycastUtil.fire();
        if (target.getType() == HitResult.Type.MISS) {
            return;
        }

        Player player = client.player;
        DataAccessor accessor = DataAccessor.INSTANCE;
        accessor.set(client.level, player, target, client.cameraEntity, client.getFrameTime());

        HudRenderer.beginBuild();

        if (target.getType() == HitResult.Type.BLOCK) {
            Block block = accessor.getBlock();
            if (block instanceof LiquidBlock && !PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_FLUID)
                || !PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_BLOCK)
                || Waila.blockBlacklist.contains(block)) {
                return;
            }

            BlockState state = HudProviderHandler.getOverrideBlock(target);
            accessor.setState(state);

            TIP.clear();
            gatherBlock(accessor, TIP, HEAD);
            HudRenderer.addLines(TIP);

            TIP.clear();
            gatherBlock(accessor, TIP, BODY);
            if (Waila.config.get().getGeneral().shouldShiftForDetails() && !TIP.isEmpty() && !player.isShiftKeyDown()) {
                HudRenderer.addLine(SNEAK_DETAIL);
            } else {
                HudRenderer.addLines(TIP);
            }

            TIP.clear();
            gatherBlock(accessor, TIP, TAIL);
            HudRenderer.addLines(TIP);
        } else if (target.getType() == HitResult.Type.ENTITY) {
            if (!PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_ENTITY)
                || Waila.entityBlacklist.contains(accessor.getEntity().getType())) {
                return;
            }

            Entity targetEnt = HudProviderHandler.getOverrideEntity(target);
            accessor.setEntity(targetEnt);

            if (targetEnt != null) {
                TIP.clear();
                gatherEntity(targetEnt, accessor, TIP, HEAD);
                HudRenderer.addLines(TIP);

                TIP.clear();
                gatherEntity(targetEnt, accessor, TIP, BODY);
                if (Waila.config.get().getGeneral().shouldShiftForDetails() && !TIP.isEmpty() && !player.isShiftKeyDown()) {
                    HudRenderer.addLine(SNEAK_DETAIL);
                } else {
                    HudRenderer.addLines(TIP);
                }

                TIP.clear();
                gatherEntity(targetEnt, accessor, TIP, TAIL);
                HudRenderer.addLines(TIP);
            }
        }

        if (PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_ITEM)) {
            HudRenderer.setStack(HudProviderHandler.getDisplayItem(target));
        }

        HudRenderer.endBuild();
        HudRenderer.shouldRender = true;
    }

    protected static Narrator getNarrator() {
        return narrator == null ? narrator = Narrator.getNarrator() : narrator;
    }

}
