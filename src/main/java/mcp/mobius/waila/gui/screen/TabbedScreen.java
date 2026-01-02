package mcp.mobius.waila.gui.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import mcp.mobius.waila.mixin.TabNavigationBarAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public interface TabbedScreen {

    Supplier<List<Tab<?>>> TABS = () -> List.of(
        new Tab<>(WailaConfigScreen.TITLE, WailaConfigScreen.class, WailaConfigScreen::new),
        new Tab<>(PluginToggleScreen.TITLE, PluginToggleScreen.class, PluginToggleScreen::new),
        new Tab<>(PluginConfigScreen.TITLE, PluginConfigScreen.class, PluginConfigScreen::new),
        new Tab<>(CreditsScreen.TITLE, CreditsScreen.class, CreditsScreen::new)
    );

    static TabNavigationBar bar(Tab<?>... tabs) {
        var manager = new TabManager(t -> {}, t -> {});
        manager.setTabArea(new ScreenRectangle(0, 0, 0, 0));

        return TabNavigationBar
            .builder(manager, tabs.length)
            .addTabs(tabs)
            .build();
    }

    Screen getParent();

    default void changeTab(Runnable change) {
        change.run();
    }

    default void initBar(int width, Consumer<TabNavigationBar> addRenderableWidget, Consumer<TabButton> setInitialFocus) {
        var clazz = this.getClass();
        var tabs = TABS.get();
        var bar = bar(tabs.toArray(new Tab<?>[0]));
        var currentTab = tabs.stream()
            .map(it -> (Tab<?>) it)
            .filter(it -> it.clazz == clazz)
            .findFirst().orElse(null);

        bar.setWidth(width);
        bar.arrangeElements();
        addRenderableWidget.accept(bar);

        if (currentTab != null) {
            bar.selectTab(tabs.indexOf(currentTab), false);
            setInitialFocus.accept(((TabNavigationBarAccess) bar).wthit_currentTabButton());
        }
    }

    record Tab<T extends Screen & TabbedScreen>(
        Component title,
        Class<T> clazz,
        Function<Screen, T> ctor
    ) implements net.minecraft.client.gui.components.tabs.Tab {

        @Override
        public Component getTabTitle() {
            return title;
        }

        @Override
        public void visitChildren(Consumer<AbstractWidget> consumer) {
        }

        @Override
        public void doLayout(ScreenRectangle screenRectangle) {
            var client = Minecraft.getInstance();
            var parent = client.screen;
            if (parent != null && parent.getClass() == clazz) return;
            if (parent instanceof TabbedScreen tabbed) {
                tabbed.changeTab(() -> client.setScreen(ctor.apply(tabbed.getParent())));
            } else {
                client.setScreen(ctor.apply(parent));
            }
        }

    }

}
