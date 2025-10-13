package mcp.mobius.waila.mixin;

import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TabNavigationBar.class)
public interface TabNavigationBarAccess {

    @Invoker("currentTabButton")
    TabButton wthit_currentTabButton();

}
