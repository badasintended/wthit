package mcp.mobius.waila.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

public class CategoryEntry extends ConfigListWidget.Entry {

    private final String title;

    public CategoryEntry(String title) {
        this.title = I18n.get(title);
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
        ctx.drawCenteredString(client.font, title, rowLeft + width / 2, rowTop + height - client.font.lineHeight, 0xFFFFFF);
    }

}
