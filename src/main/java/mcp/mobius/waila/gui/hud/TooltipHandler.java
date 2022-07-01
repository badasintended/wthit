package mcp.mobius.waila.gui.hud;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.access.DataAccessor;
import mcp.mobius.waila.api.IBlacklistConfig;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.util.RaycastUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
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
import static mcp.mobius.waila.gui.hud.ComponentHandler.gatherBlock;
import static mcp.mobius.waila.gui.hud.ComponentHandler.gatherEntity;

public class TooltipHandler {

    public static final TooltipRenderer RENDERER = new ConfigTooltipRenderer();

    private static final Tooltip TOOLTIP = new Tooltip();
    private static final Component SNEAK_DETAIL = Component.translatable("tooltip.waila.sneak_for_details").withStyle(ChatFormatting.ITALIC);

    public static void tick() {
        RENDERER.shouldRender = false;

        Minecraft client = Minecraft.getInstance();
        WailaConfig.General config = Waila.CONFIG.get().getGeneral();

        if (client.level == null || !config.isDisplayTooltip()) {
            return;
        }

        if (config.getDisplayMode() == IWailaConfig.General.DisplayMode.HOLD_KEY && !WailaClient.showOverlay.isDown()) {
            return;
        }

        if (client.screen != null && !(client.screen instanceof ChatScreen)) {
            return;
        }

        if (config.isHideFromPlayerList() && client.gui.getTabList().visible) {
            return;
        }

        if (config.isHideFromDebug() && client.options.renderDebug) {
            return;
        }

        HitResult target = RaycastUtil.fire();

        if (target.getType() == HitResult.Type.MISS) {
            return;
        }

        Player player = client.player;

        if (player == null) {
            return;
        }

        DataAccessor accessor = DataAccessor.INSTANCE;
        accessor.set(client.level, player, target, client.cameraEntity, client.getFrameTime());

        RENDERER.beginBuild();

        if (target.getType() == HitResult.Type.BLOCK) {
            Block block = accessor.getBlock();

            if (block instanceof LiquidBlock) {
                if (!PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_FLUID)) {
                    return;
                }
            } else if (!PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_BLOCK)) {
                return;
            }

            if (accessor.getBlockState().is(Waila.BLOCK_BLACKLIST_TAG) || IBlacklistConfig.get().contains(block)) {
                return;
            }

            BlockEntity blockEntity = accessor.getBlockEntity();
            if (blockEntity != null && IBlacklistConfig.get().contains(blockEntity)) {
                return;
            }

            BlockState state = ComponentHandler.getOverrideBlock(target);
            if (state == IBlockComponentProvider.EMPTY_BLOCK_STATE) {
                return;
            }

            accessor.setState(state);

            TOOLTIP.clear();
            gatherBlock(accessor, TOOLTIP, HEAD);
            RENDERER.add(TOOLTIP);

            TOOLTIP.clear();
            gatherBlock(accessor, TOOLTIP, BODY);

            if (config.isShiftForDetails() && !TOOLTIP.isEmpty() && !player.isShiftKeyDown()) {
                RENDERER.add(new Line(null).with(SNEAK_DETAIL));
            } else {
                RENDERER.add(TOOLTIP);
            }

            TOOLTIP.clear();
            gatherBlock(accessor, TOOLTIP, TAIL);
            RENDERER.add(TOOLTIP);
        } else if (target.getType() == HitResult.Type.ENTITY) {
            if (!PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY)) {
                return;
            }

            Entity actualEntity = accessor.getEntity();

            if (actualEntity == null) {
                return;
            }

            if (actualEntity.getType().is(Waila.ENTITY_BLACKLIST_TAG) || IBlacklistConfig.get().contains(accessor.getEntity())) {
                return;
            }

            Entity targetEnt = ComponentHandler.getOverrideEntity(target);

            if (targetEnt == IEntityComponentProvider.EMPTY_ENTITY) {
                return;
            }

            accessor.setEntity(targetEnt);

            if (targetEnt != null) {
                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, HEAD);
                RENDERER.add(TOOLTIP);

                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, BODY);

                if (config.isShiftForDetails() && !TOOLTIP.isEmpty() && !player.isShiftKeyDown()) {
                    RENDERER.add(new Line(null).with(SNEAK_DETAIL));
                } else {
                    RENDERER.add(TOOLTIP);
                }

                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, TAIL);
                RENDERER.add(TOOLTIP);
            }
        }

        if (PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_ICON)) {
            RENDERER.setIcon(ComponentHandler.getIcon(target));
        }

        RENDERER.shouldRender = true;
        RENDERER.endBuild();
    }

    private static class ConfigTooltipRenderer extends TooltipRenderer {

        private ConfigTooltipRenderer() {
            super(true);
        }

        private WailaConfig.Overlay getOverlay() {
            return Waila.CONFIG.get().getOverlay();
        }

        @Override
        protected float getScale() {
            return getOverlay().getScale();
        }

        @Override
        protected Align.X getXAnchor() {
            return getOverlay().getPosition().getAnchor().getX();
        }

        @Override
        protected Align.Y getYAnchor() {
            return getOverlay().getPosition().getAnchor().getY();
        }

        @Override
        protected Align.X getXAlign() {
            return getOverlay().getPosition().getAlign().getX();
        }

        @Override
        protected Align.Y getYAlign() {
            return getOverlay().getPosition().getAlign().getY();
        }

        @Override
        protected int getX() {
            return getOverlay().getPosition().getX();
        }

        @Override
        protected int getY() {
            return getOverlay().getPosition().getY();
        }

        @Override
        protected boolean bossBarsOverlap() {
            return getOverlay().getPosition().isBossBarsOverlap();
        }

        @Override
        protected int getBg() {
            return getOverlay().getColor().getBackgroundColor();
        }

        @Override
        protected int getGradStart() {
            return getOverlay().getColor().getGradientStart();
        }

        @Override
        protected int getGradEnd() {
            return getOverlay().getColor().getGradientEnd();
        }

        @Override
        protected boolean enableTextToSpeech() {
            return Waila.CONFIG.get().getGeneral().isEnableTextToSpeech();
        }

        @Override
        public int getFontColor() {
            return getOverlay().getColor().getFontColor();
        }

    }

}
