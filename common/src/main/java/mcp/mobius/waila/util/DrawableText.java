package mcp.mobius.waila.util;

import java.awt.Dimension;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.data.DataAccessor;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class DrawableText implements IDrawableText {

    private static final String LMAO = "lmao";
    private static final Supplier<Dimension> ZERO = Suppliers.memoize(Dimension::new);

    protected final List<RenderContainer> renderers = new ObjectArrayList<>();

    @Override
    public IDrawableText with(ResourceLocation id, CompoundTag data) {
        renderers.add(new RenderContainer(id, data));
        return this;
    }

    @Override
    public Dimension getSize() {
        if (renderers.isEmpty())
            return ZERO.get();

        int width = 0;
        int height = 0;
        for (RenderContainer container : renderers) {
            Dimension iconSize = container.getRenderer().getSize(container.getData(), DataAccessor.INSTANCE);
            width += iconSize.width;
            height = Math.max(height, iconSize.height);
        }

        return new Dimension(width, height);
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        int xOffset = 0;
        for (RenderContainer container : renderers) {
            Dimension size = container.getRenderer().getSize(container.getData(), DataAccessor.INSTANCE);
            container.getRenderer().draw(matrices, container.getData(), DataAccessor.INSTANCE, x + xOffset, y);
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
        DrawableText n = new DrawableText();
        renderers.forEach(r -> n.with(r.id, r.data));
        return n;
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
        return "DrawableText{" +
            "renderers=" + renderers +
            '}';
    }

    public static class RenderContainer {

        private final ResourceLocation id;
        private final CompoundTag data;
        private final ITooltipRenderer renderer;

        public RenderContainer(ResourceLocation id, CompoundTag data) {
            this.id = id;
            this.data = data;
            this.renderer = Registrar.INSTANCE.renderer.get(id);
        }

        public ResourceLocation getId() {
            return id;
        }

        public CompoundTag getData() {
            return data;
        }

        public ITooltipRenderer getRenderer() {
            return renderer;
        }

        @Override
        public String toString() {
            return id.toString();
        }

    }

}
