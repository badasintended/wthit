package mcp.mobius.waila.plugin.vanilla.renderer;

import java.awt.Dimension;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import static mcp.mobius.waila.util.DisplayUtil.bindTexture;
import static net.minecraft.client.gui.DrawableHelper.drawTexture;

public class ProgressRenderer implements ITooltipRenderer {

    private static final Identifier SHEET = Waila.id("textures/sprites.png");
    private static final Supplier<Dimension> DIMENSION = Suppliers.memoize(() -> new Dimension(26, 16));

    @Override
    public Dimension getSize(NbtCompound tag, ICommonAccessor accessor) {
        return DIMENSION.get();
    }

    @Override
    public void draw(MatrixStack matrices, NbtCompound tag, ICommonAccessor accessor, int x, int y) {
        int currentValue = tag.getInt("progress");

        bindTexture(SHEET);

        // Draws the "empty" background arrow
        drawTexture(matrices, x + 2, y, 0, 16, 22, 16, 22, 32);

        int maxValue = tag.getInt("total");
        if (maxValue > 0) {
            int progress = (currentValue * 22) / maxValue;
            // Draws the "full" foreground arrow based on the progress
            drawTexture(matrices, x + 2, y, 0, 0, progress + 1, 16, 22, 32);
        }
    }

}
