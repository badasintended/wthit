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
import static mcp.mobius.waila.gui.hud.ComponentHandler.gatherBlock;
import static mcp.mobius.waila.gui.hud.ComponentHandler.gatherEntity;

public class TooltipHandler {

    private static final ConfigTooltipRendererState STATE = new ConfigTooltipRendererState();
    private static final Tooltip TOOLTIP = new Tooltip();
    private static final Line SNEAK_DETAIL = new Line(null).with(new TranslatableComponent("tooltip.waila.sneak_for_details").withStyle(ChatFormatting.ITALIC));

    public static void tick() {
        STATE.render = false;

        Minecraft client = Minecraft.getInstance();
        WailaConfig.General config = Waila.CONFIG.get().getGeneral();

        if (client.screen != null && !(client.screen instanceof ChatScreen)) {
            return;
        }

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

        TooltipRenderer.beginBuild(STATE);

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
            TooltipRenderer.add(TOOLTIP);

            TOOLTIP.clear();
            gatherBlock(accessor, TOOLTIP, BODY);

            if (config.isShiftForDetails() && !TOOLTIP.isEmpty() && !player.isShiftKeyDown()) {
                TooltipRenderer.add(new Line(null).with(SNEAK_DETAIL));
            } else {
                TooltipRenderer.add(TOOLTIP);
            }

            TOOLTIP.clear();
            gatherBlock(accessor, TOOLTIP, TAIL);
            TooltipRenderer.add(TOOLTIP);
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
                TooltipRenderer.add(TOOLTIP);

                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, BODY);

                if (config.isShiftForDetails() && !TOOLTIP.isEmpty() && !player.isShiftKeyDown()) {
                    TooltipRenderer.add(new Line(null).with(SNEAK_DETAIL));
                } else {
                    TooltipRenderer.add(TOOLTIP);
                }

                TOOLTIP.clear();
                gatherEntity(targetEnt, accessor, TOOLTIP, TAIL);
                TooltipRenderer.add(TOOLTIP);
            }
        }

        if (PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_ICON)) {
            TooltipRenderer.setIcon(ComponentHandler.getIcon(target));
        }

        STATE.render = true;
        TooltipRenderer.endBuild();
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
        public int getBg() {
            return getOverlay().getColor().getBackgroundColor();
        }

        @Override
        public int getGradStart() {
            return getOverlay().getColor().getGradientStart();
        }

        @Override
        public int getGradEnd() {
            return getOverlay().getColor().getGradientEnd();
        }

        @Override
        public boolean enableTextToSpeech() {
            return Waila.CONFIG.get().getGeneral().isEnableTextToSpeech();
        }

        @Override
        public int getFontColor() {
            return getOverlay().getColor().getFontColor();
        }

    }

}
