package mcp.mobius.waila.gui.hud;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.access.ClientAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.ITargetRedirector;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.mixin.PlayerTabOverlayAccess;
import mcp.mobius.waila.pick.PickerResults;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.ProfilerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.gui.hud.ComponentHandler.gatherBlock;
import static mcp.mobius.waila.gui.hud.ComponentHandler.gatherEntity;
import static mcp.mobius.waila.gui.hud.ComponentHandler.requestBlockData;
import static mcp.mobius.waila.gui.hud.ComponentHandler.requestEntityData;
import static mcp.mobius.waila.gui.hud.TooltipPosition.BODY;
import static mcp.mobius.waila.gui.hud.TooltipPosition.HEAD;
import static mcp.mobius.waila.gui.hud.TooltipPosition.TAIL;

public class TooltipHandler {

    private static final ConfigTooltipRendererState STATE = new ConfigTooltipRendererState();
    private static final Tooltip TOOLTIP = new Tooltip();
    private static final Component SNEAK_DETAIL = Component.translatable(Tl.Tooltip.SNEAK_FOR_DETAILS).withStyle(ChatFormatting.ITALIC);

    private enum ProcessResult {
        CONTINUE, BREAK
    }

    public static void tick() {
        tick(STATE, false);
    }

    public static boolean tick(TooltipRenderer.State state, boolean inspect) {
        try (var ignored = ProfilerUtil.profile("wthit:tick")) {
            return _tick(state, inspect);
        }
    }

    private static boolean _tick(TooltipRenderer.State state, boolean inspect) {
        STATE.render = false;

        var client = Minecraft.getInstance();
        var config = Waila.CONFIG.get().getGeneral();

        if (client.level == null) return false;
        if (client.gameMode == null) return false;

        if (!inspect) {
            if (client.options.hideGui) return false;
            if (client.screen != null && !(client.screen instanceof ChatScreen)) return false;
            if (!config.isDisplayTooltip()) return false;
            if (config.getDisplayMode() == IWailaConfig.General.DisplayMode.HOLD_KEY && !WailaClient.keyShowOverlay.isDown()) return false;
            if (config.isHideFromPlayerList() && ((PlayerTabOverlayAccess) client.gui.getTabList()).wthit_isVisible()) return false;
            if (config.isHideFromDebug() && client.debugEntries.isF3Visible()) return false;
        }

        Player player = client.player;
        if (player == null) return false;

        var camera = client.getCameraEntity();
        if (camera == null) return false;

        var frameTime = client.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        var pickRange = Math.max(player.blockInteractionRange(), player.entityInteractionRange());
        var results = PickerResults.get();
        Vec3 castOrigin = null;
        Vec3 castDirection = null;

        for (var entry : Registrar.get().raycastVectorProviders.get(Object.class)) {
            var provider = entry.instance();
            if (!provider.isEnabled(PluginConfig.CLIENT)) continue;

            castOrigin = provider.getOrigin(frameTime);
            castDirection = provider.getDirection(frameTime);
            RayCaster.cast(client.level, camera, castOrigin, castDirection, pickRange, results);
            break;
        }

        if (castOrigin == null) return false;

        for (var target : results) {
            if (processTarget(state, target, client, player, castOrigin, castDirection, pickRange, config) == ProcessResult.BREAK) break;
        }

        return true;
    }

    private static ProcessResult redirectTarget(TooltipRenderer.State state, HitResult target, TargetRedirector redirector, Minecraft client, Player player, Vec3 castOrigin, Vec3 castDirection, double pickRange, WailaConfig.General config) {
        if (redirector.nowhere) return ProcessResult.BREAK;
        if (redirector.behind) return ProcessResult.CONTINUE;

        var redirect = redirector.to;
        if (redirect == null) return ProcessResult.CONTINUE;
        if (redirect.getType() == HitResult.Type.MISS) return ProcessResult.CONTINUE;

        return processTarget(
            state, redirect, client, player,
            castOrigin.subtract(target.getLocation().subtract(redirect.getLocation())),
            castDirection, pickRange, config);
    }

    private static ProcessResult processTarget(TooltipRenderer.State state, HitResult target, Minecraft client, Player player, Vec3 castOrigin, Vec3 castDirection, double pickRange, WailaConfig.General config) {
        var accessor = ClientAccessor.INSTANCE;

        accessor.set(client.level, player, target, client.getCameraEntity(), castOrigin, castDirection, pickRange, client.getDeltaTracker().getGameTimeDeltaPartialTick(true));

        TooltipRenderer.beginBuild(state);

        if (target.getType() == HitResult.Type.BLOCK) {
            var block = accessor.getBlock();
            var blockEntity = accessor.getBlockEntity();

            var redirector = TargetRedirector.get();
            var redirectPriority = Integer.MAX_VALUE;
            @Nullable ITargetRedirector.Result redirectResult = null;

            for (var entry : Registrar.get().blockRedirect.get(block)) {
                redirectResult = entry.instance().redirect(redirector, accessor, PluginConfig.CLIENT);
                redirectPriority = entry.priority();
                if (redirectResult != null) break;
            }

            var hasBeRedirector = false;
            for (var entry : Registrar.get().blockRedirect.get(blockEntity)) {
                if (entry.priority() >= redirectPriority) break;
                if (!hasBeRedirector) {
                    hasBeRedirector = true;
                    redirector = TargetRedirector.get();
                }
                redirectResult = entry.instance().redirect(redirector, accessor, PluginConfig.CLIENT);
                if (redirectResult != null) break;
            }

            if (redirectResult != null && !redirector.self) {
                return redirectTarget(state, target, redirector, client, player, castOrigin, castDirection, pickRange, config);
            }

            if (block instanceof LiquidBlock) {
                if (!PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_FLUID)) return ProcessResult.CONTINUE;
            } else if (!PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_BLOCK)) {
                return ProcessResult.CONTINUE;
            }

            var blockState = ComponentHandler.getOverrideBlock(target);
            if (blockState == IBlockComponentProvider.EMPTY_BLOCK_STATE) return ProcessResult.CONTINUE;

            accessor.setState(blockState);

            requestBlockData(accessor);

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
            var actualEntity = accessor.getEntity();

            var redirector = TargetRedirector.get();
            @Nullable ITargetRedirector.Result redirectResult = null;

            for (var entry : Registrar.get().entityRedirect.get(actualEntity)) {
                redirectResult = entry.instance().redirect(redirector, accessor, PluginConfig.CLIENT);
                if (redirectResult != null) break;
            }

            if (redirectResult != null && !redirector.self) {
                return redirectTarget(state, target, redirector, client, player, castOrigin, castDirection, pickRange, config);
            }

            if (!PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY)) return ProcessResult.CONTINUE;

            if (actualEntity == null) return ProcessResult.CONTINUE;

            var targetEnt = ComponentHandler.getOverrideEntity(target);
            if (targetEnt == IEntityComponentProvider.EMPTY_ENTITY) return ProcessResult.CONTINUE;

            accessor.setEntity(targetEnt);
            if (targetEnt == null) return ProcessResult.CONTINUE;

            requestEntityData(targetEnt, accessor);

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
        return ProcessResult.BREAK;
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
        public int getFps() {
            return getOverlay().getFps();
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
