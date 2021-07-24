package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.hud.HudTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.world.level.block.Blocks;

public class FabricHudTickHandler extends HudTickHandler {

    public static void registerListener() {
        WailaTooltipEvent.WAILA_HANDLE_TOOLTIP.register(event -> {
            if (!Waila.config.get().getGeneral().shouldDisplayTooltip())
                return;

            if (getNarrator().active() || !Waila.config.get().getGeneral().shouldEnableTextToSpeech())
                return;

            if (event.getCurrentTip().isEmpty())
                return;

            if (Minecraft.getInstance().screen != null && !(Minecraft.getInstance().screen instanceof ChatScreen))
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
