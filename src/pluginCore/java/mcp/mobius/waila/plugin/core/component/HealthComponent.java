package mcp.mobius.waila.plugin.core.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

public class HealthComponent implements ITooltipComponent {

    private final float health;
    private final float maxHealth;

    private final int heartsPerLine;
    private final int lineCount;

    public HealthComponent(float health, float maxHealth) {
        this.health = health;
        this.maxHealth = maxHealth;

        float maxHearts = IWailaConfig.get().getGeneral().getMaxHeartsPerLine();
        heartsPerLine = (int) (Math.min(maxHearts, Math.ceil(maxHealth)));
        lineCount = (int) (Math.ceil(maxHealth / maxHearts));
    }

    @Override
    public int getWidth() {
        return 8 * heartsPerLine;
    }

    @Override
    public int getHeight() {
        return 10 * lineCount;
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        int xOffset = 0;
        for (int i = 1; i <= Mth.ceil(maxHealth); i++) {
            if (i <= Mth.floor(health)) {
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

        void render(PoseStack matrices, int x, int y) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
            RenderSystem.enableBlend();
            if (bu != -1) {
                GuiComponent.blit(matrices, x, y, 8, 8, bu, bv, bsu, bsv, 256, 256);
            }
            GuiComponent.blit(matrices, x, y, 8, 8, u, v, su, sv, 256, 256);
            RenderSystem.disableBlend();
        }

    }

}
