package mcp.mobius.waila.gui.widget;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import mcp.mobius.waila.gui.screen.ConfigScreen;
import mcp.mobius.waila.gui.widget.value.BooleanValue;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import mcp.mobius.waila.gui.widget.value.CycleValue;
import mcp.mobius.waila.gui.widget.value.EnumValue;
import mcp.mobius.waila.gui.widget.value.InputValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;

public class ConfigListWidget extends ElementListWidget<ConfigListWidget.Entry> {

    private final ConfigScreen owner;
    private final Runnable diskWriter;

    public ConfigListWidget(ConfigScreen owner, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, Runnable diskWriter) {
        super(client, width, height, top, bottom, itemHeight);

        this.owner = owner;
        this.diskWriter = diskWriter;

        setRenderBackground(false);
    }

    public ConfigListWidget(ConfigScreen owner, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        this(owner, client, width, height, top, bottom, itemHeight, null);
    }

    @Override
    public int getRowWidth() {
        return Math.min(width - 5, 300);
    }

    @Override
    protected int getScrollbarPositionX() {
        return getRowRight() + 5;
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
        if (entry instanceof ConfigValue) {
            Element element = ((ConfigValue<?>) entry).getListener();
            if (element != null)
                owner.addListener(element);
        }
        addEntry(entry);
    }

    public ConfigListWidget with(Entry entry) {
        add(entry);
        return this;
    }

    public ConfigListWidget withButton(String title, ButtonWidget button) {
        add(new ButtonEntry(title, button));
        return this;
    }

    public ConfigListWidget withButton(String title, int width, int height, ButtonWidget.PressAction pressAction) {
        add(new ButtonEntry(title, width, height, pressAction));
        return this;
    }

    public ConfigListWidget withBoolean(String optionName, boolean value, Consumer<Boolean> save) {
        add(new BooleanValue(optionName, value, save));
        return this;
    }

    public ConfigListWidget withCycle(String optionName, String[] values, String selected, Consumer<String> save, boolean createLocale) {
        add(new CycleValue(optionName, values, selected, save, createLocale));
        return this;
    }

    public ConfigListWidget withCycle(String optionName, String[] values, String selected, Consumer<String> save) {
        add(new CycleValue(optionName, values, selected, save));
        return this;
    }

    public <T extends Enum<T>> ConfigListWidget withEnum(String optionName, T[] values, T selected, Consumer<T> save) {
        add(new EnumValue<>(optionName, values, selected, save));
        return this;
    }

    public <T> ConfigListWidget withInput(String optionName, T value, Consumer<T> save, Predicate<String> validator) {
        add(new InputValue<>(optionName, value, save, validator));
        return this;
    }

    public <T> ConfigListWidget withInput(String optionName, T value, Consumer<T> save) {
        add(new InputValue<>(optionName, value, save));
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
        public List<? extends Selectable> method_37025() {
            return Collections.emptyList();
        }

        @Override
        public abstract void render(MatrixStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime);

    }

}
