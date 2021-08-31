package mcp.mobius.waila.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class DisplayUtil extends GuiComponent {

    // because some function in DrawableHelper are not static
    private static final DisplayUtil DH = new DisplayUtil();

    private static final String NUM_SUFFIXES = "kmbt";
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static void bindTexture(ResourceLocation texture) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
    }

    public static void renderStack(int x, int y, ItemStack stack) {
        renderStack(x, y, stack, stack.getCount() > 1 ? shortHandNumber(stack.getCount()) : "");
    }

    public static void renderStack(int x, int y, ItemStack stack, String countText) {
        enable3DRender();
        try {
            CLIENT.getItemRenderer().renderGuiItem(stack, x, y);
            CLIENT.getItemRenderer().renderGuiItemDecorations(CLIENT.font, stack, x, y, countText);
        } catch (Exception e) {
            String stackStr = stack != null ? stack.toString() : "NullStack";
            ExceptionUtil.dump(e, "renderStack | " + stackStr, null);
        }
        enable2DRender();
    }

    private static String shortHandNumber(int number) {
        if (number < 1000) {
            return "" + number;
        }

        int exp = (int) Math.log(number / 1000.0);
        return String.format("%.1f%c", number / Math.pow(1000, exp), NUM_SUFFIXES.charAt(exp - 1));
    }

    public static void enable3DRender() {
        Lighting.setupFor3DItems();
        RenderSystem.enableDepthTest();
    }

    public static void enable2DRender() {
        Lighting.setupForFlatItems();
        RenderSystem.disableDepthTest();
    }

    public static void drawGradientRect(PoseStack matrices, int x, int y, int w, int h, int startColor, int endColor) {
        DH.fillGradient(matrices, x, y, x + w, y + h, startColor, endColor);
    }

    public static void drawTexturedModalRect(PoseStack matrices, int x, int y, int textureX, int textureY, int width, int height, int tw, int th) {
        blit(matrices, x, y, width, height, textureX, textureY, tw, th, 256, 256);
    }

}
