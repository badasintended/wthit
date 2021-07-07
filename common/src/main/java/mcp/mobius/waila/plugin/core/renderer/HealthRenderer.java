package mcp.mobius.waila.plugin.core.renderer;

import java.awt.Dimension;

import com.mojang.blaze3d.systems.RenderSystem;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.util.DisplayUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;

public class HealthRenderer implements ITooltipRenderer {

    @Override
    public Dimension getSize(NbtCompound tag, ICommonAccessor accessor) {
        float maxHearts = Waila.config.get().getGeneral().getMaxHeartsPerLine();
        float maxHealth = tag.getFloat("max");

        int heartsPerLine = (int) (Math.min(maxHearts, Math.ceil(maxHealth)));
        int lineCount = (int) (Math.ceil(maxHealth / maxHearts));

        return new Dimension(8 * heartsPerLine, 10 * lineCount);
    }

    @Override
    public void draw(MatrixStack matrices, NbtCompound tag, ICommonAccessor accessor, int x, int y) {
        float maxHearts = Waila.config.get().getGeneral().getMaxHeartsPerLine();
        float health = tag.getFloat("health");
        float maxHealth = tag.getFloat("max");

        int heartCount = MathHelper.ceil(maxHealth);
        int heartsPerLine = (int) (Math.min(maxHearts, Math.ceil(maxHealth)));

        int xOffset = 0;
        for (int i = 1; i <= heartCount; i++) {
            if (i <= MathHelper.floor(health)) {
                Texture.HEART.render(matrices, x + xOffset, y);
                xOffset += 8;
            }

            if ((i > health) && (i < health + 1)) {
                Texture.HALF_HEART.render(matrices, x + xOffset, y);
                xOffset += 8;
            }

            if (i >= health + 1) {
                Texture.EMPTY_HEART.render(matrices, x + xOffset, y);
                xOffset += 8;
            }

            if (i % heartsPerLine == 0) {
                y += 10;
                xOffset = 0;
            }
        }
    }

    enum Texture {
        HEART(52, 0, 9, 9, 16, 0, 9, 9, "a"),
        HALF_HEART(61, 0, 9, 9, 16, 0, 9, 9, "b"),
        EMPTY_HEART(16, 0, 9, 9, -1, -1, -1, -1, "c");

        final int u, v, su, sv;
        final int bu, bv, bsu, bsv;
        final String symbol;

        Texture(int u, int v, int su, int sv, int bu, int bv, int bsu, int bsv, String symbol) {
            this.u = u;
            this.v = v;
            this.su = su;
            this.sv = sv;
            this.bu = bu;
            this.bv = bv;
            this.bsu = bsu;
            this.bsv = bsv;
            this.symbol = symbol;
        }

        void render(MatrixStack matrices, int x, int y) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            DisplayUtil.bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);

            RenderSystem.enableBlend();
            if (bu != -1) {
                DisplayUtil.drawTexturedModalRect(matrices, x, y, bu, bv, 8, 8, bsu, bsv);
            }
            DisplayUtil.drawTexturedModalRect(matrices, x, y, u, v, 8, 8, su, sv);
            RenderSystem.disableBlend();
        }

    }

}
