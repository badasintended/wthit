package mcp.mobius.waila.util;

import java.text.DecimalFormat;

import com.mojang.blaze3d.systems.RenderSystem;
import mcp.mobius.waila.debug.ExceptionHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class DisplayUtil extends DrawableHelper {

    // because some function in DrawableHelper are not static
    private static final DisplayUtil DH = new DisplayUtil();

    private static final String[] NUM_SUFFIXES = new String[]{"", "k", "m", "b", "t"};
    private static final int MAX_LENGTH = 4;
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final DecimalFormat SHORT_HAND = new DecimalFormat("##0E0");

    public static void bindTexture(Identifier texture) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
    }

    public static void renderStack(int x, int y, ItemStack stack) {
        renderStack(x, y, stack, stack.getCount() > 1 ? shortHandNumber(stack.getCount()) : "");
    }

    public static void renderStack(int x, int y, ItemStack stack, String countText) {
        enable3DRender();
        try {
            CLIENT.getItemRenderer().renderGuiItemIcon(stack, x, y);
            CLIENT.getItemRenderer().renderGuiItemOverlay(CLIENT.textRenderer, stack, x, y, countText);
        } catch (Exception e) {
            String stackStr = stack != null ? stack.toString() : "NullStack";
            ExceptionHandler.handleErr(e, "renderStack | " + stackStr, null);
        }
        enable2DRender();
    }

    private static String shortHandNumber(Number number) {
        String shorthand = SHORT_HAND.format(number);
        shorthand = shorthand.replaceAll("E[0-9]", NUM_SUFFIXES[Character.getNumericValue(shorthand.charAt(shorthand.length() - 1)) / 3]);
        while (shorthand.length() > MAX_LENGTH || shorthand.matches("[0-9]+\\.[a-z]"))
            shorthand = shorthand.substring(0, shorthand.length() - 2) + shorthand.substring(shorthand.length() - 1);

        return shorthand;
    }

    public static void enable3DRender() {
        DiffuseLighting.enableGuiDepthLighting();
        RenderSystem.enableDepthTest();
    }

    public static void enable2DRender() {
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.disableDepthTest();
    }

    public static void drawGradientRect(MatrixStack matrices, int x, int y, int w, int h, int startColor, int endColor) {
        DH.fillGradient(matrices, x, y, x + w, y + h, startColor, endColor);
    }

    public static void drawTexturedModalRect(MatrixStack matrices, int x, int y, int textureX, int textureY, int width, int height, int tw, int th) {
        drawTexture(matrices, x, y, width, height, textureX, textureY, tw, th, 256, 256);
    }

}
