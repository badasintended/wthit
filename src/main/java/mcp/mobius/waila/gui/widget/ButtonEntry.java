package mcp.mobius.waila.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;

public class ButtonEntry extends ConfigListWidget.Entry {

    private final String title;
    private final Button button;

    public ButtonEntry(String title, Button button) {
        this.title = I18n.get(title);
        this.button = button;
        button.setMessage(new TextComponent(this.title));
    }

    public ButtonEntry(String title, int width, int height, Button.OnPress pressAction) {
        this(title, new Button(0, 0, width, height, TextComponent.EMPTY, pressAction));
    }

    @Override
    public void render(PoseStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        super.render(matrices, index, rowTop, rowLeft, width, height, mouseX, mouseY, hovered, deltaTime);

        client.font.drawShadow(matrices, title, rowLeft, rowTop + (height - client.font.lineHeight) / 2f, 16777215);
        this.button.x = rowLeft + width - button.getWidth();
        this.button.y = rowTop + (height - button.getHeight()) / 2;
        this.button.render(matrices, mouseX, mouseY, deltaTime);
    }

    @Override
    public boolean mouseClicked(double mouseY, double mouseX, int button) {
        if (button == 0 && this.button.isHovered()) {
            this.button.playDownSound(Minecraft.getInstance().getSoundManager());
            this.button.onPress();
            return true;
        }

        return false;
    }

}
