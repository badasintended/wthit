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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.HitResult;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;
import static mcp.mobius.waila.hud.HudProviderHandler.gatherBlock;
import static mcp.mobius.waila.hud.HudProviderHandler.gatherEntity;

public class HudTickHandler {

    protected static Narrator narrator;
    protected static String lastNarration = "";

    private static final List<Text> TIP = new TaggableList<>(TaggedText::new);
    private static final Text SNEAK_DETAIL = new TranslatableText("tooltip.waila.sneak_for_details").formatted(Formatting.ITALIC);

    public static void tickClient() {
        HudRenderer.shouldRender = false;

        MinecraftClient client = MinecraftClient.getInstance();
        WailaConfig config = Waila.config.get();

        if (client.world == null
            || !config.getGeneral().shouldDisplayTooltip()
            || config.getGeneral().getDisplayMode() == WailaConfig.General.DisplayMode.HOLD_KEY && !WailaClient.showOverlay.isPressed()
            || client.currentScreen != null && !(client.currentScreen instanceof ChatScreen)
            || config.getGeneral().shouldHideFromPlayerList() && client.options.keyPlayerList.wasPressed() && client.getNetworkHandler().getPlayerList().size() > 1
            || config.getGeneral().shouldHideFromDebug() && client.options.debugEnabled) {
            return;
        }

        HitResult target = RaycastUtil.fire();
        if (target.getType() == HitResult.Type.MISS) {
            return;
        }

        PlayerEntity player = client.player;
        DataAccessor accessor = DataAccessor.INSTANCE;
        accessor.set(client.world, player, target, client.cameraEntity, client.getTickDelta());

        HudRenderer.beginBuild();

        if (target.getType() == HitResult.Type.BLOCK) {
            Block block = accessor.getBlock();
            if (block instanceof FluidBlock && !PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_FLUID)
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
            if (Waila.config.get().getGeneral().shouldShiftForDetails() && !TIP.isEmpty() && !player.isSneaking()) {
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
                if (Waila.config.get().getGeneral().shouldShiftForDetails() && !TIP.isEmpty() && !player.isSneaking()) {
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
