package mcp.mobius.waila.gui.config.value;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.resources.I18n;

import java.util.function.Consumer;

public class OptionsEntryValueBoolean extends OptionsEntryValue<Boolean> {

    private final GuiButton button;

    public OptionsEntryValueBoolean(String optionName, boolean value, Consumer<Boolean> save) {
        super(optionName, save);

        this.button = new GuiButton(0, 0, 0, 100, 20, I18n.format("gui." + (value ? "yes" : "no"))) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                OptionsEntryValueBoolean.this.value = !OptionsEntryValueBoolean.this.value;
            }
        };
        this.value = value;
    }

    @Override
    protected void drawValue(int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.x = x + 135;
        this.button.y = y + entryHeight / 6;
        this.button.displayString = I18n.format("gui." + (value ? "yes" : "no"));
        this.button.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public IGuiEventListener getListener() {
        return button;
    }
}
