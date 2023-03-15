package mcp.mobius.waila.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.gui.screen.ConfigScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static mcp.mobius.waila.util.DisplayUtil.createButton;

public class ButtonEntry extends ConfigListWidget.Entry {

    private final String title;
    private final Button button;

    public ButtonEntry(String title, Button button) {
        this.title = I18n.get(title);
        this.button = button;
        button.setMessage(Component.translatable(this.title));
    }

    public ButtonEntry(String title, int width, int height, Button.OnPress pressAction) {
        this(title, createButton(0, 0, width, height, Component.empty(), pressAction));
    }

    @Override
    public void render(@NotNull PoseStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        super.render(matrices, index, rowTop, rowLeft, width, height, mouseX, mouseY, hovered, deltaTime);

        client.font.drawShadow(matrices, title, rowLeft, rowTop + (height - client.font.lineHeight) / 2f, 16777215);
        this.button.setX(rowLeft + width - button.getWidth());
        this.button.setY(rowTop + (height - button.getHeight()) / 2);
        this.button.render(matrices, mouseX, mouseY, deltaTime);
    }

    @Override
    public void addToScreen(ConfigScreen screen) {
        screen.addListener(button);
    }

}
