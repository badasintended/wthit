package mcp.mobius.waila.gui.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class OptionsEntryButton extends OptionsListWidget.Entry {

    private final String title;
    private final ButtonWidget button;

    public OptionsEntryButton(String title, ButtonWidget button) {
        this.title = I18n.translate(title);
        this.button = button;
        button.setMessage(new LiteralText(this.title));
    }

    @Override
    public void render(MatrixStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        client.textRenderer.drawWithShadow(matrices, title, rowLeft + 10, rowTop + (height / 4) + (client.textRenderer.fontHeight / 2), 16777215);
        this.button.x = rowLeft + 135;
        this.button.y = rowTop + height / 6;
        this.button.render(matrices, mouseX, mouseY, deltaTime);
    }

    @Override
    public boolean mouseClicked(double mouseY, double mouseX, int button) {
        if (button == 0 && this.button.isHovered()) {
            this.button.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.button.onPress();
            return true;
        }

        return false;
    }

}
