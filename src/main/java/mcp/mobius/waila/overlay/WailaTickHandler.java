package mcp.mobius.waila.overlay;

import com.mojang.text2speech.Narrator;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.api.impl.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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

            if (event.getCurrentTip().isEmpty())
                return;

            if (MinecraftClient.getInstance().currentScreen != null && !(MinecraftClient.getInstance().currentScreen instanceof ChatScreen))
                return;

            if (event.getAccessor().getBlock() == Blocks.AIR && event.getAccessor().getEntity() == null)
                return;

            String narrate = event.getCurrentTip().get(0).getString();
            if (lastNarration.equalsIgnoreCase(narrate))
                return;

            getNarrator().clear();
            getNarrator().say(narrate, true);
            lastNarration = narrate;
        });
    }

    public void renderOverlay(MatrixStack matrices) {
        OverlayRenderer.renderOverlay(matrices);
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

            List<Text> currentTip = new TaggableList<>(TaggedTextComponent::new);
            List<Text> currentTipHead = new TaggableList<>(TaggedTextComponent::new);
            List<Text> currentTipBody = new TaggableList<>(TaggedTextComponent::new);
            List<Text> currentTipTail = new TaggableList<>(TaggedTextComponent::new);

            if (target != null && target.getType() == HitResult.Type.BLOCK) {
                DataAccessor accessor = DataAccessor.INSTANCE;
                accessor.set(world, player, target);
                ItemStack targetStack = RayTracing.INSTANCE.getTargetStack(); // Here we get either the proper stack or the override
                accessor.stack = targetStack;

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

                    ItemStack displayItem = RayTracing.INSTANCE.getIdentifierStack();
                    tooltip = new Tooltip(currentTip, !displayItem.isEmpty());
                }
            }
        }

    }

    private void combinePositions(PlayerEntity player, List<Text> currentTip, List<Text> currentTipHead, List<Text> currentTipBody, List<Text> currentTipTail) {
        if (Waila.CONFIG.get().getGeneral().shouldShiftForDetails() && !currentTipBody.isEmpty() && !player.isSneaking()) {
            currentTipBody.clear();
            currentTipBody.add(new TranslatableText("tooltip.waila.sneak_for_details").setStyle(Style.EMPTY.withItalic(true)));
        }

        ((ITaggableList<Identifier, Text>) currentTip).absorb((ITaggableList<Identifier, Text>) currentTipHead);
        ((ITaggableList<Identifier, Text>) currentTip).absorb((ITaggableList<Identifier, Text>) currentTipBody);
        ((ITaggableList<Identifier, Text>) currentTip).absorb((ITaggableList<Identifier, Text>) currentTipTail);
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
