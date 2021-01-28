package mcp.mobius.waila;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import mcp.mobius.waila.gui.GuiConfigHome;

public class WailaModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return GuiConfigHome::new;
    }

}
