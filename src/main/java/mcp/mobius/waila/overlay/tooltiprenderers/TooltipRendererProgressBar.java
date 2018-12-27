package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import java.awt.Dimension;

public class TooltipRendererProgressBar implements ITooltipRenderer {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final Identifier SHEET = new Identifier(Waila.MODID, "textures/sprites.png");

    @Override
    public Dimension getSize(CompoundTag tag, ICommonAccessor accessor) {
        return new Dimension(26, 16);
    }

    @Override
    public void draw(CompoundTag tag, ICommonAccessor accessor, int x, int y) {
        int currentValue = tag.getInt("progress");
        int maxValue = tag.getInt("total");

        int progress = (currentValue * 22) / maxValue;

        CLIENT.getTextureManager().bindTexture(SHEET);

        DisplayUtil.drawTexturedModalRect(x + 2, y, 0, 16, 22, 16, 22, 16);
        DisplayUtil.drawTexturedModalRect(x + 2, y, 0, 0, progress + 1, 16, progress + 1, 16);

    }

}
