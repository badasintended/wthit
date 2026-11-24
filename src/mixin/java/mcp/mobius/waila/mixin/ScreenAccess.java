package mcp.mobius.waila.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Screen.class)
public interface ScreenAccess {

    @Accessor("INWORLD_FOOTER_SEPARATOR")
    static Identifier wthit_INWORLD_FOOTER_SEPARATOR() {
        throw new AssertionError();
    }

}
