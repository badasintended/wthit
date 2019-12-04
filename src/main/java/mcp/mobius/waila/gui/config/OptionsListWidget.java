package mcp.mobius.waila.gui.config;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.mobius.waila.gui.GuiOptions;
import mcp.mobius.waila.gui.config.value.OptionsEntryValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.List;

public class OptionsListWidget extends ElementListWidget<OptionsListWidget.Entry> {

    private final GuiOptions owner;
    private final Runnable diskWriter;

    public OptionsListWidget(GuiOptions owner, MinecraftClient client, int x, int height, int width, int y, int entryHeight, Runnable diskWriter) {
        super(client, x, height, width, y, entryHeight);

        this.owner = owner;
        this.diskWriter = diskWriter;
    }

    public OptionsListWidget(GuiOptions owner, MinecraftClient client, int x, int height, int width, int y, int entryHeight) {
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
        RenderSystem.disableLighting();
        RenderSystem.disableFog();
        Tessellator tessellator_1 = Tessellator.getInstance();
        BufferBuilder bufferBuilder_1 = tessellator_1.getBuffer();
        this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int int_5 = this.getRowLeft();
        int int_6 = this.top + 4 - (int)this.getScrollAmount();

        this.renderList(int_5, int_6, int_1, int_2, float_1);
        RenderSystem.disableDepthTest();
        this.renderHoleBackground(0, this.top, 255, 255);
        this.renderHoleBackground(this.bottom, this.height, 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        bufferBuilder_1.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder_1.vertex((double)this.left, (double)(this.top + 4), 0.0F).color(0, 0, 0, 0).texture(0.0F, 1.0F).next();
        bufferBuilder_1.vertex((double)this.right, (double)(this.top + 4), 0.0F).color(0, 0, 0, 0).texture(1.0F, 1.0F).next();
        bufferBuilder_1.vertex((double)this.right, (double)this.top, 0.0F).color(0, 0, 0, 255).texture(1.0F, 0.0F).next();
        bufferBuilder_1.vertex((double)this.left, (double)this.top, 0.0F).color(0, 0, 0, 255).texture(0.0F, 0.0F).next();
        bufferBuilder_1.vertex((double)this.left, (double)this.bottom, 0.0F).color(0, 0, 0, 255).texture(0.0F, 1.0F).next();
        bufferBuilder_1.vertex((double)this.right, (double)this.bottom, 0.0F).color(0, 0, 0, 255).texture(1.0F, 1.0F).next();
        bufferBuilder_1.vertex((double)this.right, (double)(this.bottom - 4), 0.0F).color(0, 0, 0, 0).texture(1.0F, 0.0F).next();
        bufferBuilder_1.vertex((double)this.left, (double)(this.bottom - 4), 0.0F).color(0, 0, 0, 0).texture(0.0F, 0.0F).next();
        tessellator_1.draw();
        int int_8 = Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
        if (int_8 > 0) {
            int int_9 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
            int_9 = MathHelper.clamp(int_9, 32, this.bottom - this.top - 8);
            int int_10 = (int)this.getScrollAmount() * (this.bottom - this.top - int_9) / int_8 + this.top;
            if (int_10 < this.top) {
                int_10 = this.top;
            }

            bufferBuilder_1.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
            bufferBuilder_1.vertex((double)int_3, (double)this.bottom, 0.0F).color(0, 0, 0, 255).texture(0.0F, 1.0F).next();
            bufferBuilder_1.vertex((double)int_4, (double)this.bottom, 0.0F).color(0, 0, 0, 255).texture(1.0F, 1.0F).next();
            bufferBuilder_1.vertex((double)int_4, (double)this.top, 0.0F).color(0, 0, 0, 255).texture(1.0F, 0.0F).next();
            bufferBuilder_1.vertex((double)int_3, (double)this.top, 0.0F).color(0, 0, 0, 255).texture(0.0F, 0.0F).next();
            bufferBuilder_1.vertex((double)int_3, (double)(int_10 + int_9), 0.0F).color(128, 128, 128, 255).texture(0.0F, 1.0F).next();
            bufferBuilder_1.vertex((double)int_4, (double)(int_10 + int_9), 0.0F).color(128, 128, 128, 255).texture(1.0F, 1.0F).next();
            bufferBuilder_1.vertex((double)int_4, (double)int_10, 0.0F).color(128, 128, 128, 255).texture(1.0F, 0.0F).next();
            bufferBuilder_1.vertex((double)int_3, (double)int_10, 0.0F).color(128, 128, 128, 255).texture(0.0F, 0.0F).next();
            bufferBuilder_1.vertex((double)int_3, (double)(int_10 + int_9 - 1), 0.0F).color(192, 192, 192, 255).texture(0.0F, 1.0F).next();
            bufferBuilder_1.vertex((double)(int_4 - 1), (double)(int_10 + int_9 - 1), 0.0F).color(192, 192, 192, 255).texture(1.0F, 1.0F).next();
            bufferBuilder_1.vertex((double)(int_4 - 1), (double)int_10, 0.0F).color(192, 192, 192, 255).texture(1.0F, 0.0F).next();
            bufferBuilder_1.vertex((double)int_3, (double)int_10, 0.0F).color(192, 192, 192, 255).texture(0.0F, 0.0F).next();
            tessellator_1.draw();
        }

        this.renderDecorations(int_1, int_2);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
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
            Element element = ((OptionsEntryValue) entry).getListener();
            if (element != null)
                owner.addListener(element);
        }
        addEntry(entry);
    }

    public abstract static class Entry extends ElementListWidget.Entry<Entry> {

        protected final MinecraftClient client;

        public Entry() {
            this.client = MinecraftClient.getInstance();
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }

        @Override
        public abstract void render(int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime);
    }
}
