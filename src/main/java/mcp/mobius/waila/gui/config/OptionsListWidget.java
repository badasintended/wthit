package mcp.mobius.waila.gui.config;

import com.mojang.blaze3d.platform.GlStateManager;
import mcp.mobius.waila.gui.GuiOptions;
import mcp.mobius.waila.gui.config.value.OptionsEntryValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

public class OptionsListWidget extends EntryListWidget<OptionsListWidget.Entry> {

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
    public int getEntryWidth() {
        return 250;
    }

    @Override
    public void draw(int x, int y, float partialTicks) {
        if (this.visible) {
            this.drawBackground();
            int int_3 = this.getScrollbarPosition();
            int int_4 = int_3 + 6;
            this.clampScrollY();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBufferBuilder();
            this.client.getTextureManager().bindTexture(Drawable.OPTIONS_BG);
            int int_5 = this.x1 + this.width / 2 - this.getEntryWidth() / 2 + 2;
            int int_6 = this.y1 + 4 - (int)this.scrollY;
            if (this.field_2170) {
                this.method_1940(int_5, int_6, tessellator);
            }

            this.drawEntries(int_5, int_6, x, y, partialTicks);
            GlStateManager.disableDepthTest();
            this.method_1954(0, this.y1, 255, 255);
            this.method_1954(this.y2, this.height, 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SrcBlendFactor.SRC_ALPHA, GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcBlendFactor.ZERO, GlStateManager.DstBlendFactor.ONE);
            GlStateManager.disableAlphaTest();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableTexture();
            buffer.begin(7, VertexFormats.POSITION_UV_COLOR);
            buffer.vertex((double)this.x1, (double)(this.y1 + 4), 0.0D).texture(0.0D, 1.0D).color(0, 0, 0, 0).next();
            buffer.vertex((double)this.x2, (double)(this.y1 + 4), 0.0D).texture(1.0D, 1.0D).color(0, 0, 0, 0).next();
            buffer.vertex((double)this.x2, (double)this.y1, 0.0D).texture(1.0D, 0.0D).color(0, 0, 0, 255).next();
            buffer.vertex((double)this.x1, (double)this.y1, 0.0D).texture(0.0D, 0.0D).color(0, 0, 0, 255).next();

            buffer.vertex((double)this.x1, (double)this.y2, 0.0D).texture(0.0D, 1.0D).color(0, 0, 0, 255).next();
            buffer.vertex((double)this.x2, (double)this.y2, 0.0D).texture(1.0D, 1.0D).color(0, 0, 0, 255).next();
            buffer.vertex((double)this.x2, (double)(this.y2 - 4), 0.0D).texture(1.0D, 0.0D).color(0, 0, 0, 0).next();
            buffer.vertex((double)this.x1, (double)(this.y2 - 4), 0.0D).texture(0.0D, 0.0D).color(0, 0, 0, 0).next();
            tessellator.draw();
            int int_8 = this.getMaxScrollY();
            if (int_8 > 0) {
                int int_9 = (int)((float)((this.y2 - this.y1) * (this.y2 - this.y1)) / (float)this.getMaxScrollPosition());
                int_9 = MathHelper.clamp(int_9, 32, this.y2 - this.y1 - 8);
                int int_10 = (int)this.scrollY * (this.y2 - this.y1 - int_9) / int_8 + this.y1;
                if (int_10 < this.y1) {
                    int_10 = this.y1;
                }

                buffer.begin(7, VertexFormats.POSITION_UV_COLOR);
                buffer.vertex((double)int_3, (double)this.y2, 0.0D).texture(0.0D, 1.0D).color(0, 0, 0, 255).next();
                buffer.vertex((double)int_4, (double)this.y2, 0.0D).texture(1.0D, 1.0D).color(0, 0, 0, 255).next();
                buffer.vertex((double)int_4, (double)this.y1, 0.0D).texture(1.0D, 0.0D).color(0, 0, 0, 255).next();
                buffer.vertex((double)int_3, (double)this.y1, 0.0D).texture(0.0D, 0.0D).color(0, 0, 0, 255).next();

                buffer.vertex((double)int_3, (double)(int_10 + int_9), 0.0D).texture(0.0D, 1.0D).color(128, 128, 128, 255).next();
                buffer.vertex((double)int_4, (double)(int_10 + int_9), 0.0D).texture(1.0D, 1.0D).color(128, 128, 128, 255).next();
                buffer.vertex((double)int_4, (double)int_10, 0.0D).texture(1.0D, 0.0D).color(128, 128, 128, 255).next();
                buffer.vertex((double)int_3, (double)int_10, 0.0D).texture(0.0D, 0.0D).color(128, 128, 128, 255).next();

                buffer.vertex((double)int_3, (double)(int_10 + int_9 - 1), 0.0D).texture(0.0D, 1.0D).color(192, 192, 192, 255).next();
                buffer.vertex((double)(int_4 - 1), (double)(int_10 + int_9 - 1), 0.0D).texture(1.0D, 1.0D).color(192, 192, 192, 255).next();
                buffer.vertex((double)(int_4 - 1), (double)int_10, 0.0D).texture(1.0D, 0.0D).color(192, 192, 192, 255).next();
                buffer.vertex((double)int_3, (double)int_10, 0.0D).texture(0.0D, 0.0D).color(192, 192, 192, 255).next();
                tessellator.draw();
            }

            this.method_1942(x, y);
            GlStateManager.enableTexture();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlphaTest();
            GlStateManager.disableBlend();
        }
    }

    public void save() {
        getEntries()
                .stream()
                .filter(e -> e instanceof OptionsEntryValue)
                .map(e -> (OptionsEntryValue) e)
                .forEach(OptionsEntryValue::save);
        if (diskWriter != null)
            diskWriter.run();
    }

    public void add(Entry entry) {
        if (entry instanceof OptionsEntryValue) {
            GuiEventListener listener = ((OptionsEntryValue) entry).getListener();
            if (listener != null)
                owner.addListener(listener);
        }
        addEntry(entry);
    }

    public abstract static class Entry extends EntryListWidget.Entry<Entry> {

        protected final MinecraftClient client;

        public Entry() {
            this.client = MinecraftClient.getInstance();
        }
    }
}
