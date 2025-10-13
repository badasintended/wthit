package mcp.mobius.waila.service;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.gui.screen.WailaConfigScreen;
import mcp.mobius.waila.mixed.IClientMixinService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

public class ClientMixinService implements IClientMixinService {

    @Override
    public void optionsScreenRow(GridLayout.RowHelper rowHelper) {
        if (!Waila.CONFIG.get().getGeneral().vanillaOptions()) return;

        var client = Minecraft.getInstance();
        var parent = client.screen;
        rowHelper.addChild(Button.builder(Component.literal(WailaConstants.MOD_NAME), (b) -> client.setScreen(new WailaConfigScreen(parent))).build());
    }

}
