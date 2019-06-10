package mcp.mobius.waila.gui.config;

import com.mojang.blaze3d.platform.GlStateManager;
import mcp.mobius.waila.gui.GuiOptions;
import mcp.mobius.waila.gui.config.value.OptionsEntryValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;

public class OptionsListWidget extends AbstractList<OptionsListWidget.Entry> {

    private final GuiOptions owner;
    private final Runnable diskWriter;

    public OptionsListWidget(GuiOptions owner, Minecraft client, int x, int height, int width, int y, int entryHeight, Runnable diskWriter) {
        super(client, x, height, width, y, entryHeight);

        this.owner = owner;
        this.diskWriter = diskWriter;
    }

    public OptionsListWidget(GuiOptions owner, Minecraft client, int x, int height, int width, int y, int entryHeight) {
        this(owner, client, x, height, width, y, entryHeight, null);
    }

    @Override
    public int getRowWidth() {
        return 250;
    }

    public void render(int int_1, int int_2, float float_1) {
        this.renderBackground();
        int int_3 = this.getScrollbarPosition();
        int int_4 = int_3 + 6;
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator_1 = Tessellator.getInstance();
        BufferBuilder bufferBuilder_1 = tessellator_1.getBuffer();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int int_5 = this.getRowLeft();
        int int_6 = this.y0 + 4 - (int)this.getScrollAmount();

        this.renderList(int_5, int_6, int_1, int_2, float_1);
        GlStateManager.disableDepthTest();
        this.renderHoleBackground(0, this.y0, 255, 255);
        this.renderHoleBackground(this.y1, this.height, 255, 255);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlphaTest();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture();
        bufferBuilder_1.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferBuilder_1.pos((double)this.x0, (double)(this.y0 + 4), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 0).endVertex();
        bufferBuilder_1.pos((double)this.x1, (double)(this.y0 + 4), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 0).endVertex();
        bufferBuilder_1.pos((double)this.x1, (double)this.y0, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
        bufferBuilder_1.pos((double)this.x0, (double)this.y0, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
        bufferBuilder_1.pos((double)this.x0, (double)this.y1, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        bufferBuilder_1.pos((double)this.x1, (double)this.y1, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
        bufferBuilder_1.pos((double)this.x1, (double)(this.y1 - 4), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 0).endVertex();
        bufferBuilder_1.pos((double)this.x0, (double)(this.y1 - 4), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 0).endVertex();
        tessellator_1.draw();
        int int_8 = Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
        if (int_8 > 0) {
            int int_9 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
            int_9 = MathHelper.clamp(int_9, 32, this.y1 - this.y0 - 8);
            int int_10 = (int)this.getScrollAmount() * (this.y1 - this.y0 - int_9) / int_8 + this.y0;
            if (int_10 < this.y0) {
                int_10 = this.y0;
            }

            bufferBuilder_1.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder_1.pos((double)int_3, (double)this.y1, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferBuilder_1.pos((double)int_4, (double)this.y1, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferBuilder_1.pos((double)int_4, (double)this.y0, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferBuilder_1.pos((double)int_3, (double)this.y0, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferBuilder_1.pos((double)int_3, (double)(int_10 + int_9), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            bufferBuilder_1.pos((double)int_4, (double)(int_10 + int_9), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            bufferBuilder_1.pos((double)int_4, (double)int_10, 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferBuilder_1.pos((double)int_3, (double)int_10, 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferBuilder_1.pos((double)int_3, (double)(int_10 + int_9 - 1), 0.0D).tex(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
            bufferBuilder_1.pos((double)(int_4 - 1), (double)(int_10 + int_9 - 1), 0.0D).tex(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
            bufferBuilder_1.pos((double)(int_4 - 1), (double)int_10, 0.0D).tex(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
            bufferBuilder_1.pos((double)int_3, (double)int_10, 0.0D).tex(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
            tessellator_1.draw();
        }

        this.renderDecorations(int_1, int_2);
        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();
    }

    public void save() {
        children()
                .stream()
                .filter(e -> e instanceof OptionsEntryValue)
                .map(e -> (OptionsEntryValue) e)
                .forEach(OptionsEntryValue::save);
        if (diskWriter != null)
            diskWriter.run();
    }

    public void add(Entry entry) {
        if (entry instanceof OptionsEntryValue) {
            IGuiEventListener element = ((OptionsEntryValue) entry).getListener();
            if (element != null)
                owner.addListener(element);
        }
        addEntry(entry);
    }

    public abstract static class Entry extends AbstractList.AbstractListEntry<Entry> {

        protected final Minecraft client;

        public Entry() {
            this.client = Minecraft.getInstance();
        }

        @Override
        public abstract void render(int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime);
    }
}
