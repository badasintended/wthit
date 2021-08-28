package mcp.mobius.waila.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import mcp.mobius.waila.gui.screen.HomeConfigScreen;

public class FabricWailaModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return HomeConfigScreen::new;
    }

}
