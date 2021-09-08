package mcp.mobius.waila.gui.widget;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.gui.screen.ConfigScreen;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

public class ConfigListWidget extends ContainerObjectSelectionList<ConfigListWidget.Entry> {

    private final ConfigScreen owner;
    private final Runnable diskWriter;

    public ConfigListWidget(ConfigScreen owner, Minecraft client, int width, int height, int top, int bottom, int itemHeight, Runnable diskWriter) {
        super(client, width, height, top, bottom, itemHeight - 4);

        this.owner = owner;
        this.diskWriter = diskWriter;

        setRenderBackground(false);
    }

    public ConfigListWidget(ConfigScreen owner, Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
        this(owner, client, width, height, top, bottom, itemHeight, null);
    }

    @Override
    public int getRowWidth() {
        return Math.min(width - 20, 450);
    }

    @Override
    protected int getScrollbarPosition() {
        return minecraft.getWindow().getGuiScaledWidth() - 5;
    }

    public void save() {
        children()
            .stream()
            .filter(e -> e instanceof ConfigValue)
            .map(e -> (ConfigValue<?>) e)
            .forEach(ConfigValue::save);
        if (diskWriter != null)
            diskWriter.run();
    }

    public void add(Entry entry) {
        add(children().size(), entry);
    }

    public void add(int index, Entry entry) {
        if (entry instanceof ConfigValue<?> value) {
            GuiEventListener element = value.getListener();
            if (element != null) {
                owner.addListener(element);
            }
            Button resetButton  = value.getResetButton();
            if (resetButton != null) {
                owner.addListener(resetButton);
            }
        }
        children().add(index, entry);
    }

    public ConfigListWidget with(Entry entry) {
        return with(children().size(), entry);
    }

    public ConfigListWidget with(int index, Entry entry) {
        add(index, entry);
        return this;
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        protected final Minecraft client;

        public Entry() {
            this.client = Minecraft.getInstance();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return Collections.emptyList();
        }

        @Override
        public void render(PoseStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
            if (rowTop <= mouseY && mouseY < rowTop + height + 4) {
                GuiComponent.fill(matrices, 0, rowTop - 2, client.getWindow().getGuiScaledWidth(), rowTop + height + 2, 0x22FFFFFF);
            }
        }

    }

}
