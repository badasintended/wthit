package mcp.mobius.waila.overlay;

import java.util.List;

import com.mojang.text2speech.Narrator;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.utils.TaggableList;
import mcp.mobius.waila.utils.TaggedText;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class TickHandler {

    protected static Narrator narrator;
    protected static String lastNarration = "";
    public static Tooltip tooltip = null;

    public static void tickClient() {
        tooltip = null;

        if (!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip())
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        PlayerEntity player = client.player;

        if (client.keyboard == null)
            return;

        if (world != null && player != null) {
            Raycast.fire();
            HitResult target = Raycast.getTarget();

            List<Text> currentTip = new TaggableList<>(TaggedText::new);
            List<Text> currentTipHead = new TaggableList<>(TaggedText::new);
            List<Text> currentTipBody = new TaggableList<>(TaggedText::new);
            List<Text> currentTipTail = new TaggableList<>(TaggedText::new);

            if (target != null && target.getType() == HitResult.Type.BLOCK) {
                DataAccessor accessor = DataAccessor.INSTANCE;
                accessor.set(world, player, target);

                Block block = accessor.getBlock();
                if ((block instanceof FluidBlock && !PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_FLUID)) || !PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_BLOCK) || block.isIn(Waila.blockBlacklist)) {
                    return;
                }

                ItemStack targetStack = Raycast.getTargetStack();
                accessor.setStack(targetStack);

                ComponentProvider.gatherBlock(accessor, currentTipHead, HEAD);
                ComponentProvider.gatherBlock(accessor, currentTipBody, BODY);
                ComponentProvider.gatherBlock(accessor, currentTipTail, TAIL);

                combinePositions(player, currentTip, currentTipHead, currentTipBody, currentTipTail);

                tooltip = new Tooltip(currentTip, !targetStack.isEmpty());
            } else if (target != null && target.getType() == HitResult.Type.ENTITY) {
                DataAccessor accessor = DataAccessor.INSTANCE;
                accessor.set(world, player, target);

                if (!PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_ENTITY) || accessor.getEntity().getType().isIn(Waila.entityBlacklist)) {
                    return;
                }

                Entity targetEnt = Raycast.getTargetEntity();
                accessor.setEntity(targetEnt);

                if (targetEnt != null) {
                    ComponentProvider.gatherEntity(targetEnt, accessor, currentTipHead, HEAD);
                    ComponentProvider.gatherEntity(targetEnt, accessor, currentTipBody, BODY);
                    ComponentProvider.gatherEntity(targetEnt, accessor, currentTipTail, TAIL);

                    combinePositions(player, currentTip, currentTipHead, currentTipBody, currentTipTail);

                    ItemStack displayItem = Raycast.getIdentifierStack();
                    tooltip = new Tooltip(currentTip, !displayItem.isEmpty());
                }
            }
        }
    }

    private static void combinePositions(PlayerEntity player, List<Text> currentTip, List<Text> currentTipHead, List<Text> currentTipBody, List<Text> currentTipTail) {
        if (Waila.CONFIG.get().getGeneral().shouldShiftForDetails() && !currentTipBody.isEmpty() && !player.isSneaking()) {
            currentTipBody.clear();
            currentTipBody.add(new TranslatableText("tooltip.waila.sneak_for_details").setStyle(Style.EMPTY.withItalic(true)));
        }

        ((ITaggableList<Identifier, Text>) currentTip).absorb((ITaggableList<Identifier, Text>) currentTipHead);
        ((ITaggableList<Identifier, Text>) currentTip).absorb((ITaggableList<Identifier, Text>) currentTipBody);
        ((ITaggableList<Identifier, Text>) currentTip).absorb((ITaggableList<Identifier, Text>) currentTipTail);
    }

    protected static Narrator getNarrator() {
        return narrator == null ? narrator = Narrator.getNarrator() : narrator;
    }

}
