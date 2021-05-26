package mcp.mobius.waila.gui.config;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import mcp.mobius.waila.gui.GuiOptions;
import mcp.mobius.waila.gui.config.value.OptionsEntryValue;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueBoolean;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueCycle;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueEnum;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueInput;
import net.minecraft.class_6379;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;

public class OptionsListWidget extends ElementListWidget<OptionsListWidget.Entry> {

    private final GuiOptions owner;
    private final Runnable diskWriter;

    public OptionsListWidget(GuiOptions owner, MinecraftClient client, int x, int height, int width, int y, int entryHeight, Runnable diskWriter) {
        super(client, x, height, width, y, entryHeight);

        this.owner = owner;
        this.diskWriter = diskWriter;

        setRenderBackground(false);
    }

    public OptionsListWidget(GuiOptions owner, MinecraftClient client, int x, int height, int width, int y, int entryHeight) {
        this(owner, client, x, height, width, y, entryHeight, null);
    }

    @Override
    public int getRowWidth() {
        return 250;
    }

    public void save() {
        children()
            .stream()
            .filter(e -> e instanceof OptionsEntryValue)
            .map(e -> (OptionsEntryValue<?>) e)
            .forEach(OptionsEntryValue::save);
        if (diskWriter != null)
            diskWriter.run();
    }

    public void add(Entry entry) {
        if (entry instanceof OptionsEntryValue) {
            Element element = ((OptionsEntryValue<?>) entry).getListener();
            if (element != null)
                owner.addListener(element);
        }
        addEntry(entry);
    }

    public OptionsListWidget with(Entry entry) {
        add(entry);
        return this;
    }

    public OptionsListWidget withButton(String title, ButtonWidget button) {
        add(new OptionsEntryButton(title, button));
        return this;
    }

    public OptionsListWidget withButton(String title, int width, int height, ButtonWidget.PressAction pressAction) {
        add(new OptionsEntryButton(title, width, height, pressAction));
        return this;
    }

    public OptionsListWidget withBoolean(String optionName, boolean value, Consumer<Boolean> save) {
        add(new OptionsEntryValueBoolean(optionName, value, save));
        return this;
    }

    public OptionsListWidget withCycle(String optionName, String[] values, String selected, Consumer<String> save, boolean createLocale) {
        add(new OptionsEntryValueCycle(optionName, values, selected, save, createLocale));
        return this;
    }

    public OptionsListWidget withCycle(String optionName, String[] values, String selected, Consumer<String> save) {
        add(new OptionsEntryValueCycle(optionName, values, selected, save));
        return this;
    }

    public <T extends Enum<T>> OptionsListWidget withEnum(String optionName, T[] values, T selected, Consumer<T> save) {
        add(new OptionsEntryValueEnum<>(optionName, values, selected, save));
        return this;
    }

    public <T> OptionsListWidget withInput(String optionName, T value, Consumer<T> save, Predicate<String> validator) {
        add(new OptionsEntryValueInput<>(optionName, value, save, validator));
        return this;
    }

    public <T> OptionsListWidget withInput(String optionName, T value, Consumer<T> save) {
        add(new OptionsEntryValueInput<>(optionName, value, save));
        return this;
    }

    public abstract static class Entry extends ElementListWidget.Entry<Entry> {

        protected final MinecraftClient client;

        public Entry() {
            this.client = MinecraftClient.getInstance();
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends class_6379> method_37025() {
            return Collections.emptyList();
        }

        @Override
        public abstract void render(MatrixStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime);

    }

}
