package mcp.mobius.waila.hud;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.text2speech.Narrator;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.config.WailaConfig.Overlay.Color;
import mcp.mobius.waila.data.DataAccessor;
import mcp.mobius.waila.event.EventCanceller;
import mcp.mobius.waila.hud.component.DrawableComponent;
import mcp.mobius.waila.hud.component.PairComponent;
import mcp.mobius.waila.hud.component.TaggedComponent;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;

import static mcp.mobius.waila.config.WailaConfig.Overlay.Position;
import static mcp.mobius.waila.util.DisplayUtil.drawGradientRect;
import static mcp.mobius.waila.util.DisplayUtil.enable2DRender;
import static mcp.mobius.waila.util.DisplayUtil.renderStack;

public class TooltipHandler {

    private static final Tooltip TOOLTIP = new Tooltip();
    private static final Object2IntOpenHashMap<Component> LINE_HEIGHT = new Object2IntOpenHashMap<>();

    private static final Supplier<Rectangle> RENDER_RECT = Suppliers.memoize(Rectangle::new);
    private static final Supplier<Rectangle> RECT = Suppliers.memoize(Rectangle::new);
    private static final Supplier<Narrator> NARRATOR = Suppliers.memoize(Narrator::getNarrator);

    private static final String COLON = ": ";

    static boolean shouldRender = false;

    private static String lastNarration = "";
    private static ItemStack stack = ItemStack.EMPTY;
    private static int topOffset;

    private static int colonOffset;
    private static int colonWidth;

    private static boolean started = false;

    public static void beginBuild() {
        TOOLTIP.clear();
        LINE_HEIGHT.clear();
        stack = ItemStack.EMPTY;
        topOffset = 0;
        colonOffset = 0;
        colonWidth = Minecraft.getInstance().font.width(COLON);
        started = true;
    }

    public static void add(Tooltip tooltip) {
        Preconditions.checkState(started);
        for (Component component : tooltip) {
            if (component instanceof TaggedComponent tagged) {
                TOOLTIP.set(tagged.tag, tagged.value);
            } else {
                add(component);
            }
        }
    }

    public static void add(Component line) {
        Preconditions.checkState(started);
        TOOLTIP.add(line);
        if (line instanceof PairComponent pair) {
            colonOffset = Math.max(colonOffset, Minecraft.getInstance().font.width(pair.key));
        }
    }

    public static ItemStack getStack() {
        return stack;
    }

    public static void setStack(ItemStack stack) {
        Preconditions.checkState(started);
        TooltipHandler.stack = PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_ITEM) ? stack : ItemStack.EMPTY;
    }

    public static void endBuild() {
        Preconditions.checkState(started);
        for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
            listener.onHandleTooltip(TOOLTIP, DataAccessor.INSTANCE, PluginConfig.INSTANCE);
        }

        narrateObjectName();

        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();

        float scale = Waila.config.get().getOverlay().getScale();
        Position pos = Waila.config.get().getOverlay().getPosition();

        int w = 0;
        int h = 0;
        for (Component line : TOOLTIP) {
            int lineW;
            int lineH;

            if (line instanceof DrawableComponent) {
                Dimension size = ((DrawableComponent) line).getSize();
                lineW = size.width;
                lineH = size.height;
            } else {
                if (line instanceof PairComponent pair) {
                    lineW = colonOffset + colonWidth + client.font.width(pair.value);
                } else {
                    Component value = line instanceof TaggedComponent tagged ? tagged.value : line;
                    lineW = client.font.width(value);
                }
                lineH = client.font.lineHeight + 1;
            }

            w = Math.max(w, lineW);
            h += lineH;
            LINE_HEIGHT.put(line, lineH);
        }

        topOffset = 0;
        if (!stack.isEmpty()) {
            if (h < 16) {
                topOffset = (16 - h) / 2;
            }

            w = Math.max(w, 16) + 20;
            h = Math.max(h, 16);
        }

        w += 10;
        h += 8;

        int windowW = (int) (window.getGuiScaledWidth() / scale);
        int windowH = (int) (window.getGuiScaledHeight() / scale);

        Align.X anchorX = pos.getAnchor().getX();
        Align.Y anchorY = pos.getAnchor().getY();

        Align.X alignX = pos.getAlign().getX();
        Align.Y alignY = pos.getAlign().getY();

        double x = windowW * anchorX.multiplier - w * alignX.multiplier + pos.getX();
        double y = windowH * anchorY.multiplier - h * alignY.multiplier + pos.getY();

        if (!pos.isBossBarsOverlap() && anchorX == Align.X.CENTER && anchorY == Align.Y.TOP) {
            y += Math.min(client.gui.getBossOverlay().events.size() * 19, window.getGuiScaledHeight() / 3 + 2);
        }

        RECT.get().setRect(x, y, w, h);

        started = false;
    }

    public static void render(PoseStack matrices, float delta) {
        if (!shouldRender) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        ProfilerFiller profiler = client.getProfiler();
        WailaConfig config = Waila.config.get();

        profiler.push("Waila Overlay");

        float scale = config.getOverlay().getScale();

        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().scale(scale, scale, 1.0F);
        RenderSystem.applyModelViewMatrix();

        matrices.pushPose();

        enable2DRender();

        Rectangle rect = RENDER_RECT.get();
        rect.setRect(RECT.get());

        EventCanceller canceller = EventCanceller.INSTANCE;
        canceller.setCanceled(false);
        for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
            listener.onBeforeTooltipRender(matrices, rect, DataAccessor.INSTANCE, PluginConfig.INSTANCE, canceller);
            if (canceller.isCanceled()) {
                matrices.popPose();
                RenderSystem.enableDepthTest();
                RenderSystem.getModelViewStack().popPose();
                RenderSystem.applyModelViewMatrix();
                profiler.pop();
                return;
            }
        }

        int x = rect.x;
        int y = rect.y;
        int w = rect.width;
        int h = rect.height;

        Color color = config.getOverlay().getColor();
        int bg = color.getBackgroundColor();
        int gradStart = color.getGradientStart();
        int gradEnd = color.getGradientEnd();

        drawGradientRect(matrices, x + 1, y, w - 1, 1, bg, bg);
        drawGradientRect(matrices, x + 1, y + h, w - 1, 1, bg, bg);
        drawGradientRect(matrices, x + 1, y + 1, w - 1, h - 1, bg, bg);
        drawGradientRect(matrices, x, y + 1, 1, h - 1, bg, bg);
        drawGradientRect(matrices, x + w, y + 1, 1, h - 1, bg, bg);
        drawGradientRect(matrices, x + 1, y + 2, 1, h - 3, gradStart, gradEnd);
        drawGradientRect(matrices, x + w - 1, y + 2, 1, h - 3, gradStart, gradEnd);

        drawGradientRect(matrices, x + 1, y + 1, w - 1, 1, gradStart, gradStart);
        drawGradientRect(matrices, x + 1, y + h - 1, w - 1, 1, gradEnd, gradEnd);

        RenderSystem.enableBlend();

        int textX = x + (stack.isEmpty() ? 6 : 26);
        int textY = y + 6 + topOffset;
        int fontColor = color.getFontColor();

        for (Component line : TOOLTIP) {
            if (line instanceof DrawableComponent drawable) {
                drawable.render(matrices, textX, textY, delta);
            } else if (line instanceof PairComponent pair) {
                client.font.drawShadow(matrices, pair.key, textX, textY, fontColor);
                client.font.drawShadow(matrices, COLON, textX + colonOffset, textY, fontColor);
                client.font.drawShadow(matrices, pair.value, textX + colonOffset + colonWidth, textY, fontColor);
            } else {
                Component value = line instanceof TaggedComponent tagged ? tagged.value : line;
                client.font.drawShadow(matrices, value, textX, textY, color.getFontColor());
            }

            textY += LINE_HEIGHT.getInt(line);
        }

        RenderSystem.disableBlend();
        matrices.popPose();

        for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
            listener.onAfterTooltipRender(matrices, rect, DataAccessor.INSTANCE, PluginConfig.INSTANCE);
        }

        if (!stack.isEmpty()) {
            renderStack(x + 5, y + h / 2 - 8, stack, "");
        }

        RenderSystem.enableDepthTest();
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
        profiler.pop();
    }

    private static void narrateObjectName() {
        if (!shouldRender) {
            return;
        }

        Narrator narrator = NARRATOR.get();
        if (narrator.active() || !Waila.config.get().getGeneral().isEnableTextToSpeech() || Minecraft.getInstance().screen instanceof ChatScreen) {
            return;
        }

        Component objectName = TOOLTIP.getTag(WailaConstants.OBJECT_NAME_TAG);
        if (objectName != null) {
            String narrate = objectName.getString();
            if (!lastNarration.equalsIgnoreCase(narrate)) {
                narrator.clear();
                narrator.say(narrate, true);
                lastNarration = narrate;
            }
        }
    }

}
