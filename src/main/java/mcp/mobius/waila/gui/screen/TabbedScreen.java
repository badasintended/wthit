package mcp.mobius.waila.gui.screen;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import mcp.mobius.waila.mixin.TabNavigationBarAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public interface TabbedScreen {

    Supplier<TabNavigationBar> TABS = () -> bar(
        new Tab<>(WailaConfigScreen.TITLE, WailaConfigScreen.class, WailaConfigScreen::new),
        new Tab<>(PluginToggleScreen.TITLE, PluginToggleScreen.class, PluginToggleScreen::new),
        new Tab<>(PluginConfigScreen.TITLE, PluginConfigScreen.class, PluginConfigScreen::new),
        new Tab<>(CreditsScreen.TITLE, CreditsScreen.class, CreditsScreen::new)
    );

    static TabNavigationBar bar(Tab<?>... tabs) {
        return MenuTabBar
            .builder(new TabManager(t -> {}, t -> {}, t -> {
                var tab = ((Tab<?>) t);
                var client = Minecraft.getInstance();
                var parent = client.gui.screen();
                if (parent != null && parent.getClass() == tab.clazz) return;
                if (parent instanceof TabbedScreen tabbed) {
                    tabbed.changeTab(() -> client.gui.setScreen(tab.ctor.apply(tabbed.getParent())));
                } else {
                    client.gui.setScreen(tab.ctor.apply(parent));
                }
            }, t -> {}), tabs.length)
            .addTabs(tabs)
            .build();
    }

    @Nullable Screen getParent();

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
        addRenderableWidget.accept(tabs);

        if (currentTab != null) {
            tabs.selectTab(tabs.getTabs().indexOf(currentTab), false);
            setInitialFocus.accept(((TabNavigationBarAccess) tabs).wthit_currentTabButton());
        }

        tabs.arrangeElements(width);
    }

    record Tab<T extends Screen & TabbedScreen>(
        Component title,
        Class<T> clazz,
        Function<@Nullable Screen, T> ctor
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

        @Override
        public Layout getLayout() {
            return LAYOUT;
        }

    }

    Layout LAYOUT = new Layout() {
        @Override
        public void visitChildren(Consumer<LayoutElement> layoutElementVisitor) {
        }

        @Override
        public void removeChildren() {
        }

        @Override
        public void setX(int x) {
        }

        @Override
        public void setY(int y) {
        }

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }

        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public int getHeight() {
            return 0;
        }
    };

}
