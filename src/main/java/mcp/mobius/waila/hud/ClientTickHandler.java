package mcp.mobius.waila.hud;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.IBlacklistConfig;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.data.DataAccessor;
import mcp.mobius.waila.util.RaycastUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;
import static mcp.mobius.waila.hud.ComponentHandler.gatherBlock;
import static mcp.mobius.waila.hud.ComponentHandler.gatherEntity;

public class ClientTickHandler {

    private static final Tooltip TOOLTIP = new Tooltip();
    private static final Line SNEAK_DETAIL = new Line(null).with(new TranslatableComponent("tooltip.waila.sneak_for_details").withStyle(ChatFormatting.ITALIC));

    public static void tick() {
        TooltipHandler.shouldRender = false;

        Minecraft client = Minecraft.getInstance();
        WailaConfig config = Waila.CONFIG.get();

        if (client.level == null
            || !config.getGeneral().isDisplayTooltip()
            || config.getGeneral().getDisplayMode() == IWailaConfig.General.DisplayMode.HOLD_KEY && !WailaClient.SHOW_OVERLAY.isDown()
            || client.screen != null && !(client.screen instanceof ChatScreen)
            || config.getGeneral().isHideFromPlayerList() && client.gui.getTabList().visible
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

        TooltipHandler.beginBuild();

        if (target.getType() == HitResult.Type.BLOCK) {
            Block block = accessor.getBlock();

            if (block instanceof LiquidBlock) {
                if (!PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_FLUID))
                    return;
            } else if (!PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_BLOCK))
                return;

            if (Waila.BLOCK_BLACKLIST_TAG.contains(block) || IBlacklistConfig.get().contains(block))
                return;

            BlockEntity blockEntity = accessor.getBlockEntity();
            if (blockEntity != null && IBlacklistConfig.get().contains(blockEntity))
                return;

            BlockState state = ComponentHandler.getOverrideBlock(target);
            if (state == IBlockComponentProvider.EMPTY_BLOCK_STATE)
                return;

            accessor.setState(state);

            TOOLTIP.clear();
            gatherBlock(accessor, TOOLTIP, HEAD);
            TooltipHandler.add(TOOLTIP);

            TOOLTIP.clear();
            gatherBlock(accessor, TOOLTIP, BODY);
            if (Waila.CONFIG.get().getGeneral().isShiftForDetails() && !TOOLTIP.isEmpty() && !player.isShiftKeyDown()) {
                TooltipHandler.add(SNEAK_DETAIL);
            } else {
                TooltipHandler.add(TOOLTIP);
            }

            TOOLTIP.clear();
            gatherBlock(accessor, TOOLTIP, TAIL);
            TooltipHandler.add(TOOLTIP);
        } else if (target.getType() == HitResult.Type.ENTITY) {
            if (!PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY))
                return;

            if (Waila.ENTITY_BLACKLIST_TAG.contains(accessor.getEntity().getType()) || IBlacklistConfig.get().contains(accessor.getEntity()))
                return;

            Entity targetEnt = ComponentHandler.getOverrideEntity(target);
            if (targetEnt == IEntityComponentProvider.EMPTY_ENTITY)
                return;

            accessor.setEntity(targetEnt);

            if (targetEnt != null) {
                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, HEAD);
                TooltipHandler.add(TOOLTIP);

                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, BODY);
                if (config.getGeneral().isShiftForDetails() && !TOOLTIP.isEmpty() && !player.isShiftKeyDown()) {
                    TooltipHandler.add(SNEAK_DETAIL);
                } else {
                    TooltipHandler.add(TOOLTIP);
                }

                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, TAIL);
                TooltipHandler.add(TOOLTIP);
            }
        }

        if (PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_ITEM)) {
            TooltipHandler.setStack(ComponentHandler.getDisplayItem(target));
        }

        TooltipHandler.shouldRender = true;
        TooltipHandler.endBuild();
    }

}
