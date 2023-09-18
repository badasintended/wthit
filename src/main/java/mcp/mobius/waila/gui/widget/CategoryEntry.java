package mcp.mobius.waila.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

public class CategoryEntry extends ConfigListWidget.Entry {

    public CategoryEntry(String title) {
        this.category = I18n.get(title);
    }

    @Override
    public void render(@NotNull PoseStack matrices, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime) {
//        ctx.drawString(client.font, category, rowLeft, rowTop + height - client.font.lineHeight, 0xFFFFFF);
        GuiComponent.drawCenteredString(matrices, client.font, category, rowLeft + width / 2, rowTop + height - client.font.lineHeight, 0xFFFFFF);
    }

    @Override
    protected boolean match(String filter) {
        return false;
    }

}
