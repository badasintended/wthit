package mcp.mobius.waila.overlay;

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
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
public class WailaTickHandler {

    private static WailaTickHandler _instance;
    public Tooltip tooltip = null;
    public MetaDataProvider handler = new MetaDataProvider();

    private WailaTickHandler() {
    }

    @SubscribeEvent
    public static void renderOverlay(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR)
            OverlayRenderer.renderOverlay();
    }

    @SubscribeEvent
    public static void tickClient(TickEvent.ClientTickEvent event) {
        if (!ConfigHandler.instance().showTooltip())
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

            List<String> currentTip = new TipList<String, String>();
            List<String> currentTipHead = new TipList<String, String>();
            List<String> currentTipBody = new TipList<String, String>();
            List<String> currentTipTail = new TipList<String, String>();

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

    public static WailaTickHandler instance() {
        if (_instance == null)
            _instance = new WailaTickHandler();
        return _instance;
    }
}
