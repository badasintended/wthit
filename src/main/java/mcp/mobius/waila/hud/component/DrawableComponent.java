package mcp.mobius.waila.hud.component;

import java.awt.Dimension;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import mcp.mobius.waila.api.IDrawableComponent;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.data.DataAccessor;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public final class DrawableComponent implements IDrawableComponent, MutableComponent, IDrawableText {

    private static final String LMAO = "lmao";
    private static final Supplier<Dimension> ZERO = Suppliers.memoize(Dimension::new);

    private final List<Container> containers = new ObjectArrayList<>();

    @Override
    public DrawableComponent with(ResourceLocation id, CompoundTag data) {
        containers.add(new RendererContainer(Registrar.INSTANCE.renderer.get(id), data));
        return this;
    }

    @Override
    public IDrawableComponent with(Component component) {
        containers.add(new ComponentContainer(component));
        return this;
    }

    @Override
    public Dimension getSize() {
        if (containers.isEmpty())
            return ZERO.get();

        int width = 0;
        int height = 0;
        for (Container container : containers) {
            Dimension size = container.getSize();
            width += size.width;
            height = Math.max(height, size.height);
        }

        return new Dimension(width, height);
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        int xOffset = 0;
        for (Container container : containers) {
            Dimension size = container.getSize();
            container.render(matrices, x + xOffset, y);
            xOffset += size.width;
        }
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY;
    }

    @Override
    public String getContents() {
        return LMAO;
    }

    @Override
    public List<Component> getSiblings() {
        return ObjectLists.emptyList();
    }

    @Override
    public MutableComponent plainCopy() {
        DrawableComponent drawable = new DrawableComponent();
        drawable.containers.addAll(containers);
        return drawable;
    }

    @Override
    public MutableComponent copy() {
        return plainCopy();
    }

    @Override
    public FormattedCharSequence getVisualOrderText() {
        return FormattedCharSequence.EMPTY;
    }

    @Override
    public MutableComponent setStyle(Style style) {
        return this;
    }

    @Override
    public MutableComponent append(Component text) {
        return this;
    }

    @Override
    public String toString() {
        return "DrawableComponent{" +
            "containers=" + containers +
            '}';
    }

    private interface Container {

        Dimension getSize();

        void render(PoseStack matrices, int x, int y);

    }

    private record ComponentContainer(Component component) implements Container {

        @Override
        public Dimension getSize() {
            Font font = Minecraft.getInstance().font;
            return new Dimension(font.width(component), font.lineHeight);
        }

        @Override
        public void render(PoseStack matrices, int x, int y) {
            Minecraft.getInstance().font.drawShadow(matrices, component, x, y, IWailaConfig.get().getOverlay().getColor().getFontColor());
        }

    }

    private record RendererContainer(ITooltipRenderer renderer, CompoundTag data) implements Container {

        @Override
        public Dimension getSize() {
            return renderer.getSize(data, DataAccessor.INSTANCE);
        }

        @Override
        public void render(PoseStack matrices, int x, int y) {
            renderer.draw(matrices, data, DataAccessor.INSTANCE, x, y);
        }

    }

}
