package mcp.mobius.waila.fabric;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.network.Packets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class FabricWailaClient extends WailaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerKeyBinds();

        Packets.initClient();

        HudElementRegistry.addLast(TooltipRenderer.ID, TooltipRenderer::render);
        ClientTickEvents.END_CLIENT_TICK.register(client -> onClientTick());
        ItemTooltipCallback.EVENT.register((stack, ctx, flag, tooltip) -> onItemTooltip(stack, tooltip));

        ClientConfigurationConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(WailaClient::onServerLogout));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(WailaClient::onServerLogout));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> new FabricClientCommand().register(dispatcher));

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricBuiltinThemeLoader());
    }

}
