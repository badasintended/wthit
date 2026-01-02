package mcp.mobius.waila.gui.screen;

import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class TabbedConfigScreen extends ConfigScreen implements TabbedScreen {

    public TabbedConfigScreen(Screen parent, Component title, @Nullable Runnable saver, @Nullable Runnable canceller) {
        super(parent, title, saver, canceller);
    }

    public TabbedConfigScreen(Screen parent, Component title) {
        super(parent, title);
    }

    @Override
    public Screen getParent() {
        return parent;
    }

    @Override
    public void init() {
        initBar(width, this::addRenderableWidget, this::setInitialFocus);
        super.init();
    }

    @Override
    public void changeTab(Runnable change) {
        if (!options.isChanged()) {
            change.run();
            return;
        }

        minecraft.setScreen(new ConfirmScreen(accept -> {
            if (accept) {
                if (options.save(false)) {
                    if (saver != null) saver.run();
                    change.run();
                } else {
                    minecraft.setScreen(this);
                }
            } else {
                change.run();
            }
        }, Component.translatable(Tl.Config.SAVE_PROMPT), CommonComponents.EMPTY));
    }

}
