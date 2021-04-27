package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.overlay.TickHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;

public class FabricTickHandler extends TickHandler {

    public static void registerListener() {
        WailaTooltipEvent.WAILA_HANDLE_TOOLTIP.register(event -> {
            if (!Waila.config.get().getGeneral().shouldDisplayTooltip())
                return;

            if (getNarrator().active() || !Waila.config.get().getGeneral().shouldEnableTextToSpeech())
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
