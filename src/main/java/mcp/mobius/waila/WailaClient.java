package mcp.mobius.waila;

import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.GuiConfigHome;
import mcp.mobius.waila.overlay.OverlayRenderer;
import mcp.mobius.waila.overlay.WailaTickHandler;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.IForgeKeybinding;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Waila.MODID, value = Dist.CLIENT)
public class WailaClient {

    public static IForgeKeybinding openConfig;
    public static IForgeKeybinding showOverlay;
    public static IForgeKeybinding toggleLiquid;

    // TODO Bring this back and remove the handling in the tick event when forge implements it
//    @SubscribeEvent
//    public static void onKeyPressed(InputEvent.KeyInputEvent event) {
//        if (openConfig == null || showOverlay == null || toggleLiquid == null)
//            return;
//
//        while (openConfig.getKeyBinding().isPressed()) {
//            Minecraft.getInstance().displayGuiScreen(new GuiConfigHome(null));
//        }
//
//        while (showOverlay.getKeyBinding().isPressed()) {
//            if (Waila.CONFIG.get().getGeneral().getDisplayMode() == WailaConfig.DisplayMode.TOGGLE) {
//                Waila.CONFIG.get().getGeneral().setDisplayTooltip(!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip());
//            }
//        }
//
//        while (toggleLiquid.getKeyBinding().isPressed()) {
//            Waila.CONFIG.get().getGeneral().setDisplayFluids(!Waila.CONFIG.get().getGeneral().shouldDisplayFluids());
//        }
//    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTooltip(ItemTooltipEvent event) {
        String name = String.format(Waila.CONFIG.get().getFormatting().getModName(), ModIdentification.getModInfo(event.getItemStack().getItem()).getName());
        event.getToolTip().add(new TextComponentString(name));
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            OverlayRenderer.renderOverlay();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            WailaTickHandler.instance().tickClient();

        if (openConfig == null || showOverlay == null || toggleLiquid == null)
            return;

        while (openConfig.getKeyBinding().isPressed()) {
            Minecraft.getInstance().displayGuiScreen(new GuiConfigHome(null));
        }

        while (showOverlay.getKeyBinding().isPressed()) {
            if (Waila.CONFIG.get().getGeneral().getDisplayMode() == WailaConfig.DisplayMode.TOGGLE) {
                Waila.CONFIG.get().getGeneral().setDisplayTooltip(!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip());
            }
        }

        while (toggleLiquid.getKeyBinding().isPressed()) {
            Waila.CONFIG.get().getGeneral().setDisplayFluids(!Waila.CONFIG.get().getGeneral().shouldDisplayFluids());
        }
    }
}
