package mcp.mobius.waila.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ButtonEntry extends ConfigListWidget.Entry {

    private final String title;
    private final Button button;

    public ButtonEntry(String title, Button button) {
        this(title, title, button);
    }

    public ButtonEntry(String name, String button, Button buttonWidget) {
        this.title = I18n.get(name);
        this.button = buttonWidget;
        buttonWidget.setMessage(Component.translatable(button));
    }

    public ButtonEntry(String title, int width, int height, Button.OnPress pressAction) {
        this(title, title, width, height, pressAction);
    }

    public ButtonEntry(String name, String button, int width, int height, Button.OnPress pressAction) {
        this(name, button, new Button(0, 0, width, height, Component.empty(), pressAction));
    }

    @Override
    public void render(@NotNull PoseStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        super.render(matrices, index, rowTop, rowLeft, width, height, mouseX, mouseY, hovered, deltaTime);

        client.font.drawShadow(matrices, title, rowLeft, rowTop + (height - client.font.lineHeight) / 2f, 16777215);
        this.button.x = rowLeft + width - button.getWidth();
        this.button.y = rowTop + (height - button.getHeight()) / 2;
        this.button.render(matrices, mouseX, mouseY, deltaTime);
    }

    @Override
    protected void gatherChildren(ImmutableList.Builder<GuiEventListener> children) {
        children.add(button);
    }

    @Override
    public boolean match(String filter) {
        return super.match(filter) || StringUtils.containsIgnoreCase(title, filter);
    }

}
