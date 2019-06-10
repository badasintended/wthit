package mcp.mobius.waila.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DisplayUtil {

    private static final String[] NUM_SUFFIXES = new String[]{"", "k", "m", "b", "t"};
    private static final int MAX_LENGTH = 4;
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static void renderStack(int x, int y, ItemStack stack) {
        enable3DRender();
        try {
            CLIENT.getItemRenderer().renderGuiItemIcon(stack, x, y);
            ItemStack overlayRender = stack.copy();
            overlayRender.setCount(1);
            CLIENT.getItemRenderer().renderGuiItemOverlay(CLIENT.textRenderer, overlayRender, x, y);
            renderStackSize(CLIENT.textRenderer, stack, x, y);
        } catch (Exception e) {
            String stackStr = stack != null ? stack.toString() : "NullStack";
            WailaExceptionHandler.handleErr(e, "renderStack | " + stackStr, null);
        }
        enable2DRender();
    }

    public static void renderStackSize(TextRenderer fr, ItemStack stack, int xPosition, int yPosition) {
        if (!stack.isEmpty() && stack.getCount() != 1) {
            String s = shortHandNumber(stack.getCount());

            if (stack.getCount() < 1)
                s = ChatFormat.RED + String.valueOf(stack.getCount());

            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableBlend();
            fr.drawWithShadow(s, (float) (xPosition + 19 - 2 - fr.getStringWidth(s)), (float) (yPosition + 6 + 3), 16777215);
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
            GlStateManager.enableBlend();
        }
    }

    private static String shortHandNumber(Number number) {
        String shorthand = new DecimalFormat("##0E0").format(number);
        shorthand = shorthand.replaceAll("E[0-9]", NUM_SUFFIXES[Character.getNumericValue(shorthand.charAt(shorthand.length() - 1)) / 3]);
        while (shorthand.length() > MAX_LENGTH || shorthand.matches("[0-9]+\\.[a-z]"))
            shorthand = shorthand.substring(0, shorthand.length() - 2) + shorthand.substring(shorthand.length() - 1);

        return shorthand;
    }

    public static void enable3DRender() {
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
    }

    public static void enable2DRender() {
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float zLevel = 0.0F;

        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        buffer.begin(7, VertexFormats.POSITION_COLOR);
        buffer.vertex((double) (left + right), (double) top, (double) zLevel).color(f1, f2, f3, f).next();
        buffer.vertex((double) left, (double) top, (double) zLevel).color(f1, f2, f3, f).next();
        buffer.vertex((double) left, (double) (top + bottom), (double) zLevel).color(f5, f6, f7, f4).next();
        buffer.vertex((double) (left + right), (double) (top + bottom), (double) zLevel).color(f5, f6, f7, f4).next();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int tw, int th) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        float zLevel = 0.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        buffer.begin(7, VertexFormats.POSITION_UV);
        buffer.vertex((double) (x), (double) (y + height), (double) zLevel).texture((double) ((float) (textureX) * f), (double) ((float) (textureY + th) * f1)).next();
        buffer.vertex((double) (x + width), (double) (y + height), (double) zLevel).texture((double) ((float) (textureX + tw) * f), (double) ((float) (textureY + th) * f1)).next();
        buffer.vertex((double) (x + width), (double) (y), (double) zLevel).texture((double) ((float) (textureX + tw) * f), (double) ((float) (textureY) * f1)).next();
        buffer.vertex((double) (x), (double) (y), (double) zLevel).texture((double) ((float) (textureX) * f), (double) ((float) (textureY) * f1)).next();
        tessellator.draw();
    }

    public static List<Component> itemDisplayNameMultiline(ItemStack itemstack) {
        List<Component> namelist = null;
        try {
            namelist = itemstack.getTooltip(CLIENT.player, TooltipContext.Default.NORMAL);
        } catch (Throwable ignored) {
        }

        if (namelist == null)
            namelist = new ArrayList<>();

        if (namelist.isEmpty())
            namelist.add(new TextComponent("Unnamed"));

        namelist.set(0, new TextComponent(itemstack.getRarity().formatting.toString() + namelist.get(0)));
        for (int i = 1; i < namelist.size(); i++)
            namelist.set(i, namelist.get(i));

        return namelist;
    }

    public static String itemDisplayNameShort(ItemStack itemstack) {
        List<Component> list = itemDisplayNameMultiline(itemstack);
        return String.format(Waila.CONFIG.get().getFormatting().getBlockName(), list.get(0).getFormattedText());
    }

    public static void renderIcon(int x, int y, int sx, int sy, IconUI icon) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        CLIENT.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_LOCATION);

        if (icon == null)
            return;

        if (icon.bu != -1)
            DisplayUtil.drawTexturedModalRect(x, y, icon.bu, icon.bv, sx, sy, icon.bsu, icon.bsv);
        DisplayUtil.drawTexturedModalRect(x, y, icon.u, icon.v, sx, sy, icon.su, icon.sv);
    }
}