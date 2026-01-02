package mcp.mobius.waila.gui.widget.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.util.DisplayUtil.createButton;

public class CycleValue extends ConfigValue<String, CycleValue> {

    private final Button button;
    private final boolean createLocale;
    private final List<String> values;

    public CycleValue(String optionName, String[] values, String selected, Consumer<String> save, boolean createLocale) {
        this(optionName, values, selected, null, save, createLocale);
    }

    public CycleValue(String optionName, String[] values, String selected, @Nullable String defaultValue, Consumer<String> save, boolean createLocale) {
        super(optionName, selected, defaultValue, save);

        this.createLocale = createLocale;
        this.values = Util.make(new ArrayList<>(), list -> list.addAll(Arrays.asList(values)));
        this.button = createButton(0, 0, 100, 20,
            createLocale
                ? Component.translatable(optionName + "_" + selected.replace(" ", "_").toLowerCase(Locale.ROOT))
                : Component.literal(selected),
            w -> setValue(this.values.get((this.values.indexOf(getValue()) + 1) % this.values.size())));
    }

    public void addValue(String value) {
        if (!values.contains(value)) {
            values.add(value);
        }
    }

    public void removeValue(String value) {
        var index = values.indexOf(value);
        if (index > -1) {
            values.remove(index);
            setValue(values.get(index % values.size()));
        }
    }

    @Override
    protected void drawValue(GuiGraphics ctx, int width, int height, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        button.active = !isDisabled();
        button.setX(x + width - button.getWidth());
        button.setY(y + (height - button.getHeight()) / 2);
        button.setMessage(createLocale
            ? Component.translatable(getValueTlKey())
            : Component.literal(getValue()));
        button.render(ctx, mouseX, mouseY, partialTicks);
    }

    @Override
    public GuiEventListener getListener() {
        return button;
    }

    @Override
    protected void buildSearchKey(StringBuilder sb) {
        super.buildSearchKey(sb);

        sb.append(" ");
        if (createLocale) sb.append(I18n.get(getValueTlKey()));
        else sb.append(getValue());
    }

    private String getValueTlKey() {
        return translationKey + "_" + getValue().replace(" ", "_").toLowerCase(Locale.ROOT);
    }

}
