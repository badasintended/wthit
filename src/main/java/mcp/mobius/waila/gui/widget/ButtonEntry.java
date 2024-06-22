package mcp.mobius.waila.gui.widget;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import static mcp.mobius.waila.util.DisplayUtil.createButton;

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
        this(name, button, createButton(0, 0, width, height, Component.empty(), pressAction));
    }

    @Override
    protected void drawEntry(GuiGraphics ctx, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        ctx.drawString(client.font, title, rowLeft, rowTop + (height - client.font.lineHeight) / 2, 0xFFFFFF);
        this.button.setX(rowLeft + width - button.getWidth());
        this.button.setY(rowTop + (height - button.getHeight()) / 2);
        this.button.render(ctx, mouseX, mouseY, deltaTime);
    }

    @Override
    protected void gatherChildren(ImmutableList.Builder<GuiEventListener> children) {
        children.add(button);
    }

    @Override
    protected void buildSearchKey(StringBuilder sb) {
        sb.append(title);
    }

}
