package mcp.mobius.waila.plugin.vanilla.renderer;

import java.awt.Dimension;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.util.CommonUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import static mcp.mobius.waila.util.DisplayUtil.bindTexture;
import static net.minecraft.client.gui.GuiComponent.blit;

public class ProgressRenderer implements ITooltipRenderer {

    private static final ResourceLocation SHEET = CommonUtil.id("textures/sprites.png");
    private static final Supplier<Dimension> DIMENSION = Suppliers.memoize(() -> new Dimension(26, 16));

    @Override
    public Dimension getSize(CompoundTag tag, ICommonAccessor accessor) {
        return DIMENSION.get();
    }

    @Override
    public void draw(PoseStack matrices, CompoundTag tag, ICommonAccessor accessor, int x, int y) {
        int currentValue = tag.getInt("progress");

        bindTexture(SHEET);

        // Draws the "empty" background arrow
        blit(matrices, x + 2, y, 0, 16, 22, 16, 22, 32);

        int maxValue = tag.getInt("total");
        if (maxValue > 0) {
            int progress = (currentValue * 22) / maxValue;
            // Draws the "full" foreground arrow based on the progress
            blit(matrices, x + 2, y, 0, 0, progress + 1, 16, 22, 32);
        }
    }

}
