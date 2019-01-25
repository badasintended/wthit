package mcp.mobius.waila.overlay;

import com.mojang.text2speech.Narrator;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.MetaDataProvider;
import mcp.mobius.waila.api.impl.TaggedList;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ingame.ChatGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.List;

public class WailaTickHandler {

    public static WailaTickHandler INSTANCE = new WailaTickHandler();
    private static Narrator narrator;
    private static String lastNarration = "";
    public Tooltip tooltip = null;
    public MetaDataProvider handler = new MetaDataProvider();

    private WailaTickHandler() {
        WailaTooltipEvent.WAILA_HANDLE_TOOLTIP.register(event -> {
            if (!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip())
                return;

            if (getNarrator().active() || !Waila.CONFIG.get().getGeneral().shouldEnableTextToSpeech())
                return;

            if (MinecraftClient.getInstance().currentGui != null && !(MinecraftClient.getInstance().currentGui instanceof ChatGui))
                return;

            if (event.getAccessor().getBlock() == Blocks.AIR && event.getAccessor().getEntity() == null)
                return;

            String narrate = event.getCurrentTip().get(0).getFormattedText();
            if (lastNarration.equalsIgnoreCase(narrate))
                return;

            getNarrator().clear();
            getNarrator().say(narrate);
            lastNarration = narrate;
        });
    }

    public void renderOverlay() {
        OverlayRenderer.renderOverlay();
    }

    public void tickClient() {
        if (!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip())
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        PlayerEntity player = client.player;

        if (client.keyboard == null)
            return;

        if (world != null && player != null) {
            RayTracing.INSTANCE.fire();
            HitResult target = RayTracing.INSTANCE.getTarget();

            List<TextComponent> currentTip = new TaggedList<TextComponent, Identifier>();
            List<TextComponent> currentTipHead = new TaggedList<TextComponent, Identifier>();
            List<TextComponent> currentTipBody = new TaggedList<TextComponent, Identifier>();
            List<TextComponent> currentTipTail = new TaggedList<TextComponent, Identifier>();

            if (target != null && target.getType() == HitResult.Type.BLOCK) {
                DataAccessor accessor = DataAccessor.INSTANCE;
                accessor.set(world, player, target);
                ItemStack targetStack = RayTracing.INSTANCE.getTargetStack(); // Here we get either the proper stack or the override

                if (!targetStack.isEmpty()) {
                    instance().handler.gatherBlockComponents(accessor, currentTipHead, TooltipPosition.HEAD);
                    instance().handler.gatherBlockComponents(accessor, currentTipBody, TooltipPosition.BODY);
                    instance().handler.gatherBlockComponents(accessor, currentTipTail, TooltipPosition.TAIL);

                    combinePositions(player, currentTip, currentTipHead, currentTipBody, currentTipTail);

                    tooltip = new Tooltip(currentTip, !targetStack.isEmpty());
                }
            } else if (target != null && target.getType() == HitResult.Type.ENTITY) {
                DataAccessor accessor = DataAccessor.INSTANCE;
                accessor.set(world, player, target);

                Entity targetEnt = RayTracing.INSTANCE.getTargetEntity(); // This need to be replaced by the override check.

                if (targetEnt != null) {
                    instance().handler.gatherEntityComponents(targetEnt, accessor, currentTipHead, TooltipPosition.HEAD);
                    instance().handler.gatherEntityComponents(targetEnt, accessor, currentTipBody, TooltipPosition.BODY);
                    instance().handler.gatherEntityComponents(targetEnt, accessor, currentTipTail, TooltipPosition.TAIL);

                    combinePositions(player, currentTip, currentTipHead, currentTipBody, currentTipTail);

                    tooltip = new Tooltip(currentTip, false);
                }
            }
        }

    }

    private void combinePositions(PlayerEntity player, List<TextComponent> currentTip, List<TextComponent> currentTipHead, List<TextComponent> currentTipBody, List<TextComponent> currentTipTail) {
        if (Waila.CONFIG.get().getGeneral().shouldShiftForDetails() && !currentTipBody.isEmpty() && !player.isSneaking()) {
            currentTipBody.clear();
            currentTipBody.add(new TranslatableTextComponent("tooltip.waila.sneak_for_details").setStyle(new Style().setItalic(true)));
        }

        currentTip.addAll(currentTipHead);
        currentTip.addAll(currentTipBody);
        currentTip.addAll(currentTipTail);
    }

    private static Narrator getNarrator() {
        return narrator == null ? narrator = Narrator.getNarrator() : narrator;
    }

    public static WailaTickHandler instance() {
        if (INSTANCE == null)
            INSTANCE = new WailaTickHandler();
        return INSTANCE;
    }
}
