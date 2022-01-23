package mcp.mobius.waila.plugin.test;

import java.awt.Dimension;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public enum DeprecatedTest implements IComponentProvider, ITooltipRenderer {

    INSTANCE;

    public static final ResourceLocation RENDERER = new ResourceLocation("test:deprecated.renderer");
    public static final ResourceLocation ENABLED = new ResourceLocation("test:deprecated.enabled");

    @Override
    public void appendHead(List<Component> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(ENABLED)) {
            ((ITaggableList<ResourceLocation, Component>) tooltip).setTag(WailaConstants.OBJECT_NAME_TAG, new TextComponent("testing"));
            tooltip.add(IDrawableText.of(RENDERER, new CompoundTag()));
        }
    }

    @Override
    public Dimension getSize(CompoundTag data, ICommonAccessor accessor) {
        return new Dimension(10, 10);
    }

    @Override
    public void draw(PoseStack matrices, CompoundTag data, ICommonAccessor accessor, int x, int y) {
        GuiComponent.fill(matrices, x, y, x + 10, y + 10, 0xFFFFFFFF);
    }

}
