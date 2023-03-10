package mcp.mobius.waila.gui.hud.theme;

import java.nio.file.Path;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IThemeAccessor;
import mcp.mobius.waila.api.WailaConstants;

public enum ThemeAccessor implements IThemeAccessor {

    INSTANCE;

    @Override
    public Path getPath(String path) {
        return Waila.CONFIG_DIR.resolve(WailaConstants.NAMESPACE + "/" + path);
    }

}
