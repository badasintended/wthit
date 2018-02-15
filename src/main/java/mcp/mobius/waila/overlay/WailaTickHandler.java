package mcp.mobius.waila.overlay;

import com.mojang.text2speech.Narrator;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.MetaDataProvider;
import mcp.mobius.waila.api.impl.TipList;
import mcp.mobius.waila.cbcore.Layout;
import mcp.mobius.waila.client.KeyEvent;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.util.List;

@Mod.EventBusSubscriber(modid = Waila.MODID, value = Side.CLIENT)
public class WailaTickHandler {

    private static WailaTickHandler _instance;
    private static Narrator narrator;
    private static String lastNarration = "";
    public Tooltip tooltip = null;
    public MetaDataProvider handler = new MetaDataProvider();

    private WailaTickHandler() {
    }

    @SubscribeEvent
    public static void renderOverlay(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            OverlayRenderer.renderOverlay();
    }

    @SubscribeEvent
    public static void tickClient(TickEvent.ClientTickEvent event) {
        if (!ConfigHandler.instance().showTooltip())
            return;

        if (!Keyboard.isCreated())
            return;

        if (!Keyboard.isKeyDown(KeyEvent.key_show.getKeyCode()))
            if (!ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false))
                if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, false))
                    ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, false);

        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft.world;
        EntityPlayer player = minecraft.player;

        if (world != null && player != null) {
            RayTracing.instance().fire();
            RayTraceResult target = RayTracing.instance().getTarget();

            List<String> currentTip = new TipList<>();
            List<String> currentTipHead = new TipList<>();
            List<String> currentTipBody = new TipList<>();
            List<String> currentTipTail = new TipList<>();

            if (target != null && target.typeOfHit == RayTraceResult.Type.BLOCK) {
                DataAccessorCommon accessor = DataAccessorCommon.instance;
                accessor.set(world, player, target);
                ItemStack targetStack = RayTracing.instance().getTargetStack(); // Here we get either the proper stack or the override

                if (!targetStack.isEmpty()) {
                    instance().handler.handleBlockTextData(targetStack, world, player, target, accessor, currentTipHead, Layout.HEADER);
                    instance().handler.handleBlockTextData(targetStack, world, player, target, accessor, currentTipBody, Layout.BODY);
                    instance().handler.handleBlockTextData(targetStack, world, player, target, accessor, currentTipTail, Layout.FOOTER);

                    if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHIFTBLOCK, false) && currentTipBody.size() > 0 && !accessor.getPlayer().isSneaking()) {
                        currentTipBody.clear();
                        currentTipBody.add(TextFormatting.ITALIC + "Press shift for more data");
                    }

                    currentTip.addAll(currentTipHead);
                    currentTip.addAll(currentTipBody);
                    currentTip.addAll(currentTipTail);

                    instance().tooltip = new Tooltip(currentTip, targetStack);
                }
            } else if (target != null && target.typeOfHit == RayTraceResult.Type.ENTITY) {
                DataAccessorCommon accessor = DataAccessorCommon.instance;
                accessor.set(world, player, target);

                Entity targetEnt = RayTracing.instance().getTargetEntity(); // This need to be replaced by the override check.

                if (targetEnt != null) {
                    instance().handler.handleEntityTextData(targetEnt, world, player, target, accessor, currentTipHead, Layout.HEADER);
                    instance().handler.handleEntityTextData(targetEnt, world, player, target, accessor, currentTipBody, Layout.BODY);
                    instance().handler.handleEntityTextData(targetEnt, world, player, target, accessor, currentTipTail, Layout.FOOTER);

                    if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHIFTENTS, false) && currentTipBody.size() > 0 && !accessor.getPlayer().isSneaking()) {
                        currentTipBody.clear();
                        currentTipBody.add(TextFormatting.ITALIC + "Press shift for more data");
                    }

                    currentTip.addAll(currentTipHead);
                    currentTip.addAll(currentTipBody);
                    currentTip.addAll(currentTipTail);

                    instance().tooltip = new Tooltip(currentTip, false);
                }
            }
        }

    }

    @SubscribeEvent
    public static void onTooltip(WailaTooltipEvent event) {
        if (!ConfigHandler.instance().showTooltip())
            return;

        if (!getNarrator().active())
            return;

        if (!ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_TTS, false))
            return;

        if (Minecraft.getMinecraft().currentScreen != null || Minecraft.getMinecraft().isGamePaused())
            return;

        String narrate = TextFormatting.getTextWithoutFormattingCodes(event.getCurrentTip().get(0));
        if (lastNarration.equalsIgnoreCase(narrate))
            return;

        if (event.getAccessor().getBlock() == Blocks.AIR && event.getAccessor().getEntity() == null)
            return;

        getNarrator().clear();
        getNarrator().say(narrate);
        lastNarration = narrate;
    }

    private static Narrator getNarrator() {
        return narrator == null ? narrator = Narrator.getNarrator() : narrator;
    }

    public static WailaTickHandler instance() {
        if (_instance == null)
            _instance = new WailaTickHandler();
        return _instance;
    }
}
