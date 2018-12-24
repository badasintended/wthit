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
        return new Dimension(32, 16);
    }

    @Override
    public void draw(CompoundTag tag, ICommonAccessor accessor, int x, int y) {
        int currentValue = tag.getInt("cook");
        int maxValue = 200;

        int progress = (currentValue * 28) / maxValue;

        CLIENT.getTextureManager().bindTexture(SHEET);

        DisplayUtil.drawTexturedModalRect(x + 4, y, 4, 16, 28, 16, 28, 16);
        DisplayUtil.drawTexturedModalRect(x + 4, y, 4, 0, progress + 1, 16, progress + 1, 16);

    }

}
