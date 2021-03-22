package mcp.mobius.waila.forge;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.overlay.TickHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraftforge.common.MinecraftForge;

public class ForgeTickHandler extends TickHandler {

    public static void registerListener() {
        MinecraftForge.EVENT_BUS.addListener((WailaTooltipEvent event) -> {
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

}
