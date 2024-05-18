package mcp.mobius.waila.quilt;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.network.Packets;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.server.packs.PackType;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.client.ClientConfigurationConnectionEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayConnectionEvents;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.tooltip.api.client.ItemTooltipCallback;

public class QuiltWailaClient extends WailaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {
        registerKeyBinds();

        Packets.initClient();

        HudRenderCallback.EVENT.register(TooltipRenderer::render);
        ClientTickEvents.END.register(client -> onClientTick());
        ItemTooltipCallback.EVENT.register((stack, player, context, lines) -> onItemTooltip(stack, lines));

        ClientConfigurationConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(WailaClient::onServerLogout));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(WailaClient::onServerLogout));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> new QuiltClientCommand().register(dispatcher));

        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(new QuiltBuiltinThemeLoader());
    }

}
