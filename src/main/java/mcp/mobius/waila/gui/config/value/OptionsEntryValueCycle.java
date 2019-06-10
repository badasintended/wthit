package mcp.mobius.waila.gui.config.value;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class OptionsEntryValueCycle extends OptionsEntryValue<String> {

    private final String translationKey;
    private final Button button;
    private final boolean createLocale;

    public OptionsEntryValueCycle(String optionName, String[] values, String selected, Consumer<String> save, boolean createLocale) {
        super(optionName, save);

        this.translationKey = optionName;
        this.createLocale = createLocale;
        List<String> vals = Arrays.asList(values);
        this.button = new Button(0, 0, 100, 20, createLocale ? I18n.format(optionName + "_" + selected.replace(" ", "_").toLowerCase(Locale.ROOT)) : selected, w -> {
            value = vals.get((vals.indexOf(value) + 1) % vals.size());
        });
        this.value = selected;
    }

    public OptionsEntryValueCycle(String optionName, String[] values, String selected, Consumer<String> save) {
        this(optionName, values, selected, save, false);
    }

    @Override
    protected void drawValue(int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.x = x + 135;
        this.button.y = y + entryHeight / 6;
        this.button.setMessage(createLocale ? I18n.format(translationKey + "_" + value.replace(" ", "_").toLowerCase(Locale.ROOT)) : value);
        this.button.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public IGuiEventListener getListener() {
        return button;
    }
}
