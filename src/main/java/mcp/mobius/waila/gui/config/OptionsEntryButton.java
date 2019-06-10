package mcp.mobius.waila.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

public class OptionsEntryButton extends OptionsListWidget.Entry {

    private final String title;
    private final Button button;

    public OptionsEntryButton(String title, Button button) {
        this.title = I18n.format(title);
        this.button = button;
        button.setMessage(this.title);
    }

    @Override
    public void render(int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        client.fontRenderer.drawStringWithShadow(title, rowLeft + 10, rowTop + (height / 4) + (client.fontRenderer.FONT_HEIGHT / 2), 16777215);
        this.button.x = rowLeft + 135;
        this.button.y = rowTop + height / 6;
        this.button.render(mouseX, mouseY, deltaTime);
    }

    @Override
    public boolean mouseClicked(double mouseY, double mouseX, int button) {
        if (button == 0 && this.button.isHovered()) {
            this.button.playDownSound(Minecraft.getInstance().getSoundHandler());
            this.button.onPress();
            return true;
        }

        return false;
    }
}
