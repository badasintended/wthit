package mcp.mobius.waila.gui.config.value;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Locale;
import java.util.function.Consumer;

public class OptionsEntryValueEnum<T extends Enum<T>> extends OptionsEntryValue<T> {

    private final String translationKey;
    private final ButtonWidget button;

    public OptionsEntryValueEnum(String optionName, T[] values, T selected, Consumer<T> save) {
        super(optionName, save);

        this.translationKey = optionName;
        this.button = new ButtonWidget(0, 0, 100, 20, new TranslatableText(optionName + "_" + selected.name().toLowerCase(Locale.ROOT)), w -> {
            value = values[(value.ordinal() + 1) % values.length];
        });
        this.value = selected;
    }

    @Override
    protected void drawValue(MatrixStack matrices, int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.x = x + 135;
        this.button.y = y + entryHeight / 6;
        this.button.setMessage(new TranslatableText(translationKey + "_" + value.name().toLowerCase(Locale.ROOT)));
        this.button.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public Element getListener() {
        return button;
    }
}
