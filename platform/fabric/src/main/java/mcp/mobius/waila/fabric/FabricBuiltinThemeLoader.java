package mcp.mobius.waila.fabric;

import mcp.mobius.waila.gui.hud.theme.BuiltinThemeLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public class FabricBuiltinThemeLoader extends BuiltinThemeLoader implements IdentifiableResourceReloadListener {

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

}
