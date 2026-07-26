package mcp.mobius.waila.service;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.config.input.KeyBind;
import mcp.mobius.waila.config.input.WrappedKeyBind;
import mcp.mobius.waila.gui.screen.WailaConfigScreen;
import mcp.mobius.waila.mixed.IClientMixinService;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

public class ClientMixinService implements IClientMixinService {

    @Override
    public void optionsScreenRow(GridLayout.RowHelper rowHelper) {
        if (!WailaClient.CONFIG.get().getGeneral().vanillaOptions()) return;

        var client = Minecraft.getInstance();
        var parent = client.gui.screen();
        rowHelper.addChild(Button.builder(Component.literal(WailaConstants.MOD_NAME), (b) -> client.gui.setScreen(new WailaConfigScreen(parent))).build());
    }

    WailaConfig.KeyBinds binds() {
        return WailaClient.CONFIG.get().getKeyBinds();
    }

    @Override
    public List<KeyMapping> getWrappedBinds() {
        var def = new WailaConfig().getKeyBinds();
        return List.of(
            new WrappedKeyBind(Tl.Key.CONFIG, def.getOpenConfig(), val -> binds().setOpenConfig(val), () -> binds().getOpenConfig()),
            new WrappedKeyBind(Tl.Key.SHOW_OVERLAY, def.getShowOverlay(), val -> binds().setShowOverlay(val), () -> binds().getShowOverlay()),
            new WrappedKeyBind(Tl.Key.TOGGLE_LIQUID, def.getToggleLiquid(), val -> binds().setToggleLiquid(val), () -> binds().getToggleLiquid()),
            new WrappedKeyBind(Tl.Key.SHOW_RECIPE_INPUT, def.getShowRecipeInput(), val -> binds().setShowRecipeInput(val), () -> binds().getShowRecipeInput()),
            new WrappedKeyBind(Tl.Key.SHOW_RECIPE_OUTPUT, def.getShowRecipeOutput(), val -> binds().setShowRecipeOutput(val), () -> binds().getShowRecipeOutput())
        );
    }

    @Override
    public void saveConfig() {
        WailaClient.CONFIG.save();
    }

    @Override
    public void onButton(InputConstants.Key key, boolean pressed) {
        KeyBind.set(key, pressed);
    }

}
