package mcp.mobius.waila.gui.widget.value;

import java.util.Locale;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.TranslatableComponent;

public class EnumValue<T extends Enum<T>> extends ConfigValue<T> {

    private final String translationKey;
    private final Button button;

    public EnumValue(String optionName, T[] values, T selected, Consumer<T> save) {
        super(optionName, save);

        this.translationKey = optionName;
        this.button = new Button(0, 0, 100, 20, new TranslatableComponent(optionName + "_" + selected.name().toLowerCase(Locale.ROOT)), w ->
            value = values[(value.ordinal() + 1) % values.length]);
        this.value = selected;
    }

    @Override
    protected void drawValue(PoseStack matrices, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.x = x + width - button.getWidth();
        this.button.y = y + (height - button.getHeight()) / 2;
        this.button.setMessage(new TranslatableComponent(translationKey + "_" + value.name().toLowerCase(Locale.ROOT)));
        this.button.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public GuiEventListener getListener() {
        return button;
    }

}
