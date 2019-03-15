package mcp.mobius.waila.gui.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.function.Consumer;

public class OptionsEntryButton extends OptionsListWidget.Entry {

    private final String title;
    private final ButtonWidget button;

    public OptionsEntryButton(String title, ButtonWidget button) {
        this.title = I18n.translate(title);
        this.button = button;
        button.setText(this.title);
    }

    @Override
    public void draw(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean selected, float partialTicks) {
        client.textRenderer.drawWithShadow(title, getX() + 10, getY() + (entryHeight / 4) + (client.textRenderer.fontHeight / 2), 16777215);
        this.button.x = getX() + 135;
        this.button.y = getY() + entryHeight / 6;
        this.button.draw(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseY, double mouseX, int button) {
        if (button == 0 && this.button.isHovered()) {
            this.button.playPressedSound(MinecraftClient.getInstance().getSoundLoader());
            this.button.onPressed();
            return true;
        }

        return false;
    }
}
