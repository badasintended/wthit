package mcp.mobius.waila.gui.config.value;

import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.Locale;
import java.util.function.Consumer;

public class OptionsEntryValueEnum<T extends Enum<T>> extends OptionsEntryValue<T> {

    private final String translationKey;
    private final ButtonWidget button;

    public OptionsEntryValueEnum(String optionName, T[] values, T selected, Consumer<T> save) {
        super(optionName, save);

        this.translationKey = optionName;
        this.button = new ButtonWidget(0, 0, 0, 100, 20, I18n.translate(optionName + "_" + selected.name().toLowerCase(Locale.ROOT))) {
            @Override
            public void onPressed(double mouseX, double mouseY) {
                value = values[(value.ordinal() + 1) % values.length];
            }
        };
        this.value = selected;
    }

    @Override
    protected void drawValue(int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.x = x + 135;
        this.button.y = y + entryHeight / 6;
        this.button.text = I18n.translate(translationKey + "_" + value.name().toLowerCase(Locale.ROOT));
        this.button.draw(mouseX, mouseY, partialTicks);
    }

    @Override
    public GuiEventListener getListener() {
        return button;
    }
}
