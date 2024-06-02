package mcp.mobius.waila.plugin.test;

import java.awt.Rectangle;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum EventListenerTest implements IEventListener {

    INSTANCE;

    static final ResourceLocation HANDLE_TOOLTIP = ResourceLocation.parse("test:event.handle_tooltip");
    static final ResourceLocation BEFORE_RENDER = ResourceLocation.parse("test:event.before_render");
    static final ResourceLocation AFTER_RENDER = ResourceLocation.parse("test:event.after_render");
    static final ResourceLocation ITEM_MOD_NAME = ResourceLocation.parse("test:event.item_mod_name");

    @Override
    public void onHandleTooltip(ITooltip tooltip, ICommonAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(HANDLE_TOOLTIP)) {
            tooltip.addLine(Component.literal("EventListenerTest"));
            tooltip.setLine(WailaConstants.MOD_NAME_TAG, Component.literal("EventListenerTest"));
        }
    }

    @Override
    public void onBeforeTooltipRender(GuiGraphics ctx, Rectangle rect, ICommonAccessor accessor, IPluginConfig config, Canceller canceller) {
        if (config.getBoolean(BEFORE_RENDER)) {
            ctx.fill(rect.x, rect.y, rect.x + 20, rect.y + 20, 0xFF0000FF);
            rect.setLocation(rect.x + 25, rect.y + 25);
        }
    }

    @Override
    public void onAfterTooltipRender(GuiGraphics ctx, Rectangle rect, ICommonAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(AFTER_RENDER)) {
            ctx.fill(rect.x, rect.y, rect.x + 20, rect.y + 20, 0xFF00FFFF);
        }
    }

    @Nullable
    @Override
    public String getHoveredItemModName(ItemStack stack, IPluginConfig config) {
        if (config.getBoolean(ITEM_MOD_NAME)) {
            return "EventListenerTest";
        }
        return null;
    }

}
