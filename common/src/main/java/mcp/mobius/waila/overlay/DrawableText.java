package mcp.mobius.waila.overlay;

import java.awt.Dimension;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.ITooltipRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

public class DrawableText implements IDrawableText {

    private static final String LMAO = "lmao";
    private static final Lazy<Dimension> ZERO = new Lazy<>(Dimension::new);

    protected final List<RenderContainer> renderers = new ObjectArrayList<>();

    @Override
    public IDrawableText with(Identifier id, NbtCompound data) {
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
    public void render(MatrixStack matrices, int x, int y, float delta) {
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
    public String asString() {
        return LMAO;
    }

    @Override
    public List<Text> getSiblings() {
        return ObjectLists.emptyList();
    }

    @Override
    public MutableText copy() {
        DrawableText n = new DrawableText();
        renderers.forEach(r -> n.with(r.id, r.data));
        return n;
    }

    @Override
    public MutableText shallowCopy() {
        return copy();
    }

    @Override
    public OrderedText asOrderedText() {
        return OrderedText.EMPTY;
    }

    @Override
    public MutableText setStyle(Style style) {
        return this;
    }

    @Override
    public MutableText append(Text text) {
        return this;
    }

    @Override
    public String toString() {
        return "DrawableText{" +
            "renderers=" + renderers +
            '}';
    }

    public static class RenderContainer {

        private final Identifier id;
        private final NbtCompound data;
        private final ITooltipRenderer renderer;

        public RenderContainer(Identifier id, NbtCompound data) {
            this.id = id;
            this.data = data;
            this.renderer = TooltipRegistrar.INSTANCE.renderer.get(id);
        }

        public Identifier getId() {
            return id;
        }

        public NbtCompound getData() {
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
