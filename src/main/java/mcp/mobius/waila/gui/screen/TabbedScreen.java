package mcp.mobius.waila.gui.screen;

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

    Supplier<TabNavigationBar> TABS = () -> bar(
        new Tab<>(WailaConfigScreen.TITLE, WailaConfigScreen.class, WailaConfigScreen::new),
        new Tab<>(PluginToggleScreen.TITLE, PluginToggleScreen.class, PluginToggleScreen::new),
        new Tab<>(PluginConfigScreen.TITLE, PluginConfigScreen.class, PluginConfigScreen::new),
        new Tab<>(CreditsScreen.TITLE, CreditsScreen.class, CreditsScreen::new)
    );

    static TabNavigationBar bar(Tab<?>... tabs) {
        return TabNavigationBar
            .builder(new TabManager(t -> {}, t -> {}, t -> {
                var tab = ((Tab<?>) t);
                var client = Minecraft.getInstance();
                var parent = client.screen;
                if (parent != null && parent.getClass() == tab.clazz) return;
                if (parent instanceof TabbedScreen tabbed) {
                    tabbed.changeTab(() -> client.setScreen(tab.ctor.apply(tabbed.getParent())));
                } else {
                    client.setScreen(tab.ctor.apply(parent));
                }
            }, t -> {}), tabs.length)
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
        var currentTab = tabs.getTabs().stream()
            .map(it -> (Tab<?>) it)
            .filter(it -> it.clazz == clazz)
            .findFirst().orElse(null);

        tabs.setWidth(width);
        tabs.arrangeElements();
        addRenderableWidget.accept(tabs);

        if (currentTab != null) {
            tabs.selectTab(tabs.getTabs().indexOf(currentTab), false);
            setInitialFocus.accept(((TabNavigationBarAccess) tabs).wthit_currentTabButton());
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
        public Component getTabExtraNarration() {
            return Component.empty();
        }

        @Override
        public void visitChildren(Consumer<AbstractWidget> consumer) {
        }

        @Override
        public void doLayout(ScreenRectangle screenRectangle) {
        }

    }

}
