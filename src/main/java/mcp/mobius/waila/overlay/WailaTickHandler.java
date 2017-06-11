package mcp.mobius.waila.overlay;

import com.mojang.text2speech.Narrator;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class WailaTickHandler {

    //public static LangUtil lang = LangUtil.loadLangDir("waila");

    private static WailaTickHandler _instance;
    public Tooltip tooltip = null;
    public MetaDataProvider handler = new MetaDataProvider();
    private int ticks = 0;
    //public ItemStack identifiedHighlight = new ItemStack(Blocks.dirt);
    private List<String> currenttip = new TipList<String, String>();
    private List<String> currenttipHead = new TipList<String, String>();
    private List<String> currenttipBody = new TipList<String, String>();
    private List<String> currenttipTail = new TipList<String, String>();
    private Minecraft mc = Minecraft.getMinecraft();

    private WailaTickHandler() {
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void tickRender(TickEvent.RenderTickEvent event) {
        OverlayRenderer.renderOverlay();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void tickClient(TickEvent.ClientTickEvent event) {

        if (!Keyboard.isKeyDown(KeyEvent.key_show.getKeyCode()) &&
                !ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false) &&
                ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, false)) {
            ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, false);
        }


        World world = mc.world;
        EntityPlayer player = mc.player;
        if (world != null && player != null) {
            RayTracing.instance().fire();
            RayTraceResult target = RayTracing.instance().getTarget();

            if (target != null && target.typeOfHit == RayTraceResult.Type.BLOCK) {
                DataAccessorCommon accessor = DataAccessorCommon.instance;
                accessor.set(world, player, target);
                ItemStack targetStack = RayTracing.instance().getTargetStack();    // Here we get either the proper stack or the override

                if (targetStack != null) {
                    this.currenttip = new TipList<String, String>();
                    this.currenttipHead = new TipList<String, String>();
                    this.currenttipBody = new TipList<String, String>();
                    this.currenttipTail = new TipList<String, String>();


                    //this.identifiedHighlight = handler.identifyHighlight(world, player, target);
                    this.currenttipHead = handler.handleBlockTextData(targetStack, world, player, target, accessor, currenttipHead, Layout.HEADER);
                    this.currenttipBody = handler.handleBlockTextData(targetStack, world, player, target, accessor, currenttipBody, Layout.BODY);
                    this.currenttipTail = handler.handleBlockTextData(targetStack, world, player, target, accessor, currenttipTail, Layout.FOOTER);

                    if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHIFTBLOCK, false) && currenttipBody.size() > 0 && !accessor.getPlayer().isSneaking()) {
                        currenttipBody.clear();
                        currenttipBody.add(TextFormatting.ITALIC + "Press shift for more data");
                    }

                    this.currenttip.addAll(this.currenttipHead);
                    this.currenttip.addAll(this.currenttipBody);
                    this.currenttip.addAll(this.currenttipTail);

                    this.tooltip = new Tooltip(this.currenttip, targetStack);
                }
            } else if (target != null && target.typeOfHit == RayTraceResult.Type.ENTITY) {
                DataAccessorCommon accessor = DataAccessorCommon.instance;
                accessor.set(world, player, target);

                Entity targetEnt = RayTracing.instance().getTargetEntity(); // This need to be replaced by the override check.

                if (targetEnt != null) {
                    this.currenttip = new TipList<String, String>();
                    this.currenttipHead = new TipList<String, String>();
                    this.currenttipBody = new TipList<String, String>();
                    this.currenttipTail = new TipList<String, String>();

                    this.currenttipHead = handler.handleEntityTextData(targetEnt, world, player, target, accessor, currenttipHead, Layout.HEADER);
                    this.currenttipBody = handler.handleEntityTextData(targetEnt, world, player, target, accessor, currenttipBody, Layout.BODY);
                    this.currenttipTail = handler.handleEntityTextData(targetEnt, world, player, target, accessor, currenttipTail, Layout.FOOTER);

                    if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHIFTENTS, false) && currenttipBody.size() > 0 && !accessor.getPlayer().isSneaking()) {
                        currenttipBody.clear();
                        currenttipBody.add(TextFormatting.ITALIC + "Press shift for more data");
                    }

                    this.currenttip.addAll(this.currenttipHead);
                    this.currenttip.addAll(this.currenttipBody);
                    this.currenttip.addAll(this.currenttipTail);

                    this.tooltip = new Tooltip(this.currenttip, false);
                }
            }
        }

    }

    private String lastNarration;

    @SubscribeEvent
    public void onTooltip(WailaTooltipEvent event) {
        String narrate = TextFormatting.getTextWithoutFormattingCodes(event.getCurrentTip().get(0));
        if (narrate.equalsIgnoreCase(lastNarration))
            return;

        if (event.getAccessor().getBlock() == Blocks.AIR && event.getAccessor().getEntity() == null)
            return;

        Narrator.getNarrator().say(narrate);
        lastNarration = narrate;
    }

    public static WailaTickHandler instance() {
        if (_instance == null)
            _instance = new WailaTickHandler();
        return _instance;
    }
}
