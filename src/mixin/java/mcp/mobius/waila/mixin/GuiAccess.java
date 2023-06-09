package mcp.mobius.waila.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Gui.class)
public interface GuiAccess {

    @Accessor("GUI_ICONS_LOCATION")
    static ResourceLocation wthit_guiIconsLocation() {
        throw new AssertionError("mixin");
    }

}
