package mcp.mobius.waila.overlay;

import java.util.List;

import com.mojang.text2speech.Narrator;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.MetaDataProvider;
import mcp.mobius.waila.api.impl.TaggableList;
import mcp.mobius.waila.api.impl.TaggedTextComponent;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
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

public class TickHandler {

    public static TickHandler INSTANCE = new TickHandler();
    protected static Narrator narrator;
    protected static String lastNarration = "";
    public Tooltip tooltip = null;
    public MetaDataProvider handler = new MetaDataProvider();

    public void renderOverlay(MatrixStack matrices) {
        OverlayRenderer.renderOverlay(matrices);
    }

    public void tickClient() {
        tooltip = null;

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

                Block block = accessor.block;
                if ((block instanceof FluidBlock && !PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_FLUID)) || !PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_BLOCK) || block.isIn(Waila.blockBlacklist)) {
                    return;
                }

                ItemStack targetStack = RayTracing.INSTANCE.getTargetStack(); // Here we get either the proper stack or the override
                accessor.stack = targetStack;

                instance().handler.gatherBlockComponents(accessor, currentTipHead, TooltipPosition.HEAD);
                instance().handler.gatherBlockComponents(accessor, currentTipBody, TooltipPosition.BODY);
                instance().handler.gatherBlockComponents(accessor, currentTipTail, TooltipPosition.TAIL);

                combinePositions(player, currentTip, currentTipHead, currentTipBody, currentTipTail);

                tooltip = new Tooltip(currentTip, !targetStack.isEmpty());
            } else if (target != null && target.getType() == HitResult.Type.ENTITY) {
                DataAccessor accessor = DataAccessor.INSTANCE;
                accessor.set(world, player, target);

                if (!PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_ENTITY) || accessor.entity.getType().isIn(Waila.entityBlacklist)) {
                    return;
                }

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

    protected static Narrator getNarrator() {
        return narrator == null ? narrator = Narrator.getNarrator() : narrator;
    }

    public static TickHandler instance() {
        if (INSTANCE == null)
            INSTANCE = new TickHandler();
        return INSTANCE;
    }

}
