package mcp.mobius.waila.fabric;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.hud.TooltipHandler;
import mcp.mobius.waila.network.Packets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class FabricWailaClient extends WailaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerKeyBinds();

        Packets.initClient();

        HudRenderCallback.EVENT.register(TooltipHandler::render);
        ClientTickEvents.END_CLIENT_TICK.register(client -> onClientTick());
        ItemTooltipCallback.EVENT.register((stack, ctx, tooltip) -> onItemTooltip(stack, tooltip));
        ClientPlayConnectionEvents.INIT.register((handler, client) -> client.execute(() -> onServerLogIn(handler.getConnection())));
    }

}
