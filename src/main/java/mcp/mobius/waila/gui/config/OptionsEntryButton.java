package mcp.mobius.waila.gui.config;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class OptionsEntryButton extends OptionsListWidget.Entry {

    private final String title;
    private final GuiButton button;

    public OptionsEntryButton(String title, GuiButton button) {
        this.title = I18n.format(title);
        this.button = button;
        button.displayString = this.title;
    }

    @Override
    public void drawEntry(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean selected, float partialTicks) {
        client.fontRenderer.drawStringWithShadow(title, getX() + 10, getY() + (entryHeight / 4) + (client.fontRenderer.FONT_HEIGHT / 2), 16777215);
        this.button.x = getX() + 135;
        this.button.y = getY() + entryHeight / 6;
        this.button.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseY, double mouseX, int button) {
        if (button == 0 && this.button.isMouseOver()) {
            this.button.onClick(mouseY, mouseX);
            return true;
        }

        return false;
    }
}
