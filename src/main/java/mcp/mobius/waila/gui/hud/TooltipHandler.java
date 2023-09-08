package mcp.mobius.waila.gui.hud;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.access.DataAccessor;
import mcp.mobius.waila.api.IBlacklistConfig;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.mixin.PlayerTabOverlayAccess;
import mcp.mobius.waila.pick.PickerAccessor;
import mcp.mobius.waila.pick.PickerResults;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.HitResult;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;
import static mcp.mobius.waila.gui.hud.ComponentHandler.gatherBlock;
import static mcp.mobius.waila.gui.hud.ComponentHandler.gatherEntity;

public class TooltipHandler {

    private static final ConfigTooltipRendererState STATE = new ConfigTooltipRendererState();
    private static final Tooltip TOOLTIP = new Tooltip();
    private static final Component SNEAK_DETAIL = Component.translatable(Tl.Tooltip.SNEAK_FOR_DETAILS).withStyle(ChatFormatting.ITALIC);

    public static void tick() {
        STATE.render = false;

        var client = Minecraft.getInstance();
        var config = Waila.CONFIG.get().getGeneral();

        if (client.options.hideGui) {
            return;
        }

        if (client.screen != null && !(client.screen instanceof ChatScreen)) {
            return;
        }

        if (client.level == null || !config.isDisplayTooltip()) {
            return;
        }

        if (config.getDisplayMode() == IWailaConfig.General.DisplayMode.HOLD_KEY && !WailaClient.keyShowOverlay.isDown()) {
            return;
        }

        if (config.isHideFromPlayerList() && ((PlayerTabOverlayAccess) client.gui.getTabList()).wthit_isVisible()) {
            return;
        }

        if (config.isHideFromDebug() && client.getDebugOverlay().showDebugScreen()) {
            return;
        }

        if (client.gameMode == null) {
            return;
        }

        Player player = client.player;

        if (player == null) {
            return;
        }

        var results = PickerResults.get();
        Registrar.INSTANCE.picker.pick(PickerAccessor.of(client, client.cameraEntity, client.gameMode.getPickRange(), client.getFrameTime()), results, PluginConfig.CLIENT);

        for (var target : results) {
            var accessor = DataAccessor.INSTANCE;
            accessor.set(client.level, player, target, client.cameraEntity, client.getFrameTime());

            TooltipRenderer.beginBuild(STATE);

            if (target.getType() == HitResult.Type.BLOCK) {
                var block = accessor.getBlock();

                if (block instanceof LiquidBlock) {
                    if (!PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_FLUID)) {
                        continue;
                    }
                } else if (!PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_BLOCK)) {
                    continue;
                }

                if (IBlacklistConfig.get().contains(block)) {
                    continue;
                }

                var blockEntity = accessor.getBlockEntity();
                if (blockEntity != null && IBlacklistConfig.get().contains(blockEntity)) {
                    continue;
                }

                var state = ComponentHandler.getOverrideBlock(target);
                if (state == IBlockComponentProvider.EMPTY_BLOCK_STATE) {
                    continue;
                }

                accessor.setState(state);

                TOOLTIP.clear();
                gatherBlock(accessor, TOOLTIP, HEAD);
                TooltipRenderer.add(TOOLTIP);

                TOOLTIP.clear();
                gatherBlock(accessor, TOOLTIP, BODY);

                if (config.isShiftForDetails() && !TOOLTIP.isEmpty() && !player.isShiftKeyDown()) {
                    if (!config.isHideShiftText()) {
                        TooltipRenderer.add(new Line(null).with(SNEAK_DETAIL));
                    }
                } else {
                    TooltipRenderer.add(TOOLTIP);
                }

                TOOLTIP.clear();
                gatherBlock(accessor, TOOLTIP, TAIL);
            } else if (target.getType() == HitResult.Type.ENTITY) {
                if (!PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY)) {
                    continue;
                }

                var actualEntity = accessor.getEntity();

                if (actualEntity == null) {
                    continue;
                }

                if (IBlacklistConfig.get().contains(actualEntity)) {
                    continue;
                }

                var targetEnt = ComponentHandler.getOverrideEntity(target);

                if (targetEnt == IEntityComponentProvider.EMPTY_ENTITY) {
                    continue;
                }

                accessor.setEntity(targetEnt);

                if (targetEnt == null) {
                    continue;
                }

                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, HEAD);
                TooltipRenderer.add(TOOLTIP);

                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, BODY);

                if (config.isShiftForDetails() && !TOOLTIP.isEmpty() && !player.isShiftKeyDown()) {
                    if (!config.isHideShiftText()) {
                        TooltipRenderer.add(new Line(null).with(SNEAK_DETAIL));
                    }
                } else {
                    TooltipRenderer.add(TOOLTIP);
                }

                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, TAIL);
            }

            TooltipRenderer.add(TOOLTIP);

            if (PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_ICON)) {
                TooltipRenderer.setIcon(ComponentHandler.getIcon(target));
            }

            STATE.render = true;
            TooltipRenderer.endBuild();

            break;
        }
    }

    private static class ConfigTooltipRendererState implements TooltipRenderer.State {

        private boolean render;

        @Override
        public boolean render() {
            return render;
        }

        @Override
        public boolean fireEvent() {
            return true;
        }

        private WailaConfig.Overlay getOverlay() {
            return Waila.CONFIG.get().getOverlay();
        }

        @Override
        public float getScale() {
            return getOverlay().getScale();
        }

        @Override
        public Align.X getXAnchor() {
            return getOverlay().getPosition().getAnchor().getX();
        }

        @Override
        public Align.Y getYAnchor() {
            return getOverlay().getPosition().getAnchor().getY();
        }

        @Override
        public Align.X getXAlign() {
            return getOverlay().getPosition().getAlign().getX();
        }

        @Override
        public Align.Y getYAlign() {
            return getOverlay().getPosition().getAlign().getY();
        }

        @Override
        public int getX() {
            return getOverlay().getPosition().getX();
        }

        @Override
        public int getY() {
            return getOverlay().getPosition().getY();
        }

        @Override
        public boolean bossBarsOverlap() {
            return getOverlay().getPosition().isBossBarsOverlap();
        }

        @Override
        public ITheme getTheme() {
            return getOverlay().getColor().getTheme();
        }

        @Override
        public int getBackgroundAlpha() {
            return getOverlay().getColor().getBackgroundAlpha();
        }

        @Override
        public boolean enableTextToSpeech() {
            return Waila.CONFIG.get().getGeneral().isEnableTextToSpeech();
        }

    }

}
