package mcp.mobius.waila.gui.widget.value;

import java.util.Locale;
import java.util.function.Consumer;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class EnumValue<T extends Enum<T>> extends ConfigValue<T> {

    private final String translationKey;
    private final ButtonWidget button;

    public EnumValue(String optionName, T[] values, T selected, Consumer<T> save) {
        super(optionName, save);

        this.translationKey = optionName;
        this.button = new ButtonWidget(0, 0, 100, 20, new TranslatableText(optionName + "_" + selected.name().toLowerCase(Locale.ROOT)), w ->
            value = values[(value.ordinal() + 1) % values.length]);
        this.value = selected;
    }

    @Override
    protected void drawValue(MatrixStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.x = x + width - button.getWidth();
        this.button.y = y + (height - button.getHeight()) / 2;
        this.button.setMessage(new TranslatableText(translationKey + "_" + value.name().toLowerCase(Locale.ROOT)));
        this.button.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public Element getListener() {
        return button;
    }

}
