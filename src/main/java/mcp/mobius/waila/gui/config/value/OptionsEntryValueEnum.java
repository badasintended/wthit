package mcp.mobius.waila.gui.config.value;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.resources.I18n;

import java.util.Locale;
import java.util.function.Consumer;

public class OptionsEntryValueEnum<T extends Enum<T>> extends OptionsEntryValue<T> {

    private final String translationKey;
    private final GuiButton button;

    public OptionsEntryValueEnum(String optionName, T[] values, T selected, Consumer<T> save) {
        super(optionName, save);

        this.translationKey = optionName;
        this.button = new GuiButton(0, 0, 0, 100, 20, I18n.format(optionName + "_" + selected.name().toLowerCase(Locale.ROOT))) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                value = values[(value.ordinal() + 1) % values.length];
            }
        };
        this.value = selected;
    }

    @Override
    protected void drawValue(int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.x = x + 135;
        this.button.y = y + entryHeight / 6;
        this.button.displayString = I18n.format(translationKey + "_" + value.name().toLowerCase(Locale.ROOT));
        this.button.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public IGuiEventListener getListener() {
        return button;
    }
}
