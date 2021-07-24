package mcp.mobius.waila.forge;

import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.plugin.core.WailaCore;
import mcp.mobius.waila.plugin.vanilla.WailaVanilla;

@SuppressWarnings("unused")
public class ForgePlugins {

    @WailaPlugin(id = "waila:core")
    public static class Core extends WailaCore {

    }

    @WailaPlugin(id = "waila:vanilla")
    public static class Vanilla extends WailaVanilla {

    }

}
