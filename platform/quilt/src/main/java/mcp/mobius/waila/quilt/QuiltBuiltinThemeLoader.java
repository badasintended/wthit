package mcp.mobius.waila.quilt;

import mcp.mobius.waila.gui.hud.theme.BuiltinThemeLoader;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

public class QuiltBuiltinThemeLoader extends BuiltinThemeLoader implements IdentifiableResourceReloader {

    @Override
    public @NotNull ResourceLocation getQuiltId() {
        return ID;
    }

}
