package mcp.mobius.waila.gui.widget.value;

import java.util.Locale;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.util.DisplayUtil.createButton;

public class EnumValue<T extends Enum<T>> extends ConfigValue<T, EnumValue<T>> {

    private final Button button;

    public EnumValue(String optionName, T[] values, T selected, @Nullable T defaultValue, Consumer<T> save) {
        super(optionName, selected, defaultValue, save);

        this.button = createButton(0, 0, 100, 20, Component.translatable(optionName + "_" + selected.name().toLowerCase(Locale.ROOT)), button -> {
            setValue(values[(getValue().ordinal() + 1) % values.length]);
            button.setMessage(Component.translatable(getValueTlKey()));
        });
    }

    @Override
    protected void drawValue(GuiGraphics ctx, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        button.active = !isDisabled();
        button.setX(x + width - button.getWidth());
        button.setY(y + (height - button.getHeight()) / 2);
        button.render(ctx, mouseX, mouseY, partialTicks);
    }

    @Override
    public @NotNull Button getListener() {
        return button;
    }

    @Override
    protected void buildSearchKey(StringBuilder sb) {
        super.buildSearchKey(sb);
        sb.append(" ").append(I18n.get(getValueTlKey()));
    }

    private String getValueTlKey() {
        return translationKey + "_" + getValue().name().toLowerCase(Locale.ROOT);
    }

}
