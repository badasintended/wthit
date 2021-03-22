package mcp.mobius.waila.overlay;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.utils.ExceptionHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class DisplayUtil extends DrawableHelper {

    // because some function in DrawableHelper are not static
    private static final DisplayUtil DH = new DisplayUtil();

    private static final String[] NUM_SUFFIXES = new String[]{"", "k", "m", "b", "t"};
    private static final int MAX_LENGTH = 4;
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final DecimalFormat SHORT_HAND = new DecimalFormat("##0E0");
    private static final LiteralText UNNAMED = new LiteralText("Unnamed");

    public static void bind(Identifier texture) {
        RenderSystem.setShader(GameRenderer::method_34542);
        RenderSystem.setShaderTexture(0, texture);
    }

    public static void renderStack(int x, int y, ItemStack stack) {
        enable3DRender();
        try {
            CLIENT.getItemRenderer().renderGuiItemIcon(stack, x, y);
            CLIENT.getItemRenderer().renderGuiItemOverlay(CLIENT.textRenderer, stack, x, y, stack.getCount() > 1 ? shortHandNumber(stack.getCount()) : "");
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

    public static List<Text> itemDisplayNameMultiline(ItemStack itemstack) {
        List<Text> namelist = null;
        try {
            namelist = itemstack.getTooltip(CLIENT.player, TooltipContext.Default.NORMAL);
        } catch (Throwable ignored) {
        }

        if (namelist == null)
            namelist = new ArrayList<>();

        if (namelist.isEmpty())
            namelist.add(UNNAMED);

        namelist.set(0, new LiteralText(itemstack.getRarity().formatting.toString() + namelist.get(0)));
        for (int i = 1; i < namelist.size(); i++)
            namelist.set(i, namelist.get(i));

        return namelist;
    }

    public static String itemDisplayNameShort(ItemStack itemstack) {
        List<Text> list = itemDisplayNameMultiline(itemstack);
        return String.format(Waila.getConfig().get().getFormatting().getBlockName(), list.get(0).getString());
    }

    public static void renderIcon(MatrixStack matrices, int x, int y, int sx, int sy, IconUI icon) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        bind(GUI_ICONS_TEXTURE);

        if (icon == null)
            return;

        RenderSystem.enableBlend();
        if (icon.bu != -1)
            DisplayUtil.drawTexturedModalRect(matrices, x, y, icon.bu, icon.bv, sx, sy, icon.bsu, icon.bsv);
        DisplayUtil.drawTexturedModalRect(matrices, x, y, icon.u, icon.v, sx, sy, icon.su, icon.sv);
        RenderSystem.disableBlend();
    }

}