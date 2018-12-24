package mcp.mobius.waila.gui.config;

import net.minecraft.client.resource.language.I18n;

public class OptionsEntryText extends OptionsListWidget.Entry {

    private final String title;
    private final int width;

    public OptionsEntryText(String title) {
        this.title = I18n.translate(title);
        this.width = client.fontRenderer.getStringWidth(title);
    }

    @Override
    public void draw(int var1, int var2, int var3, int var4, boolean var5, float var6) {
        client.fontRenderer.drawWithShadow(title, (float)(client.currentGui.width / 2 - width / 2), (float)(this.getY() + var2 - client.fontRenderer.fontHeight - 1), 16777215);
    }
}
