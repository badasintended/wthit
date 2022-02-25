package mcp.mobius.waila.hud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.ITooltipLine;
import mcp.mobius.waila.api.component.WrappedComponent;
import mcp.mobius.waila.hud.component.DrawableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

// TODO: Remove MutableComponent interface
public class Line implements ITooltipLine, MutableComponent {

    @Nullable
    public final ResourceLocation tag;
    public final List<ITooltipComponent> components = new ArrayList<>();

    private int width = -1;
    private int height;

    public Line(@Nullable ResourceLocation tag) {
        this.tag = tag;
    }

    @Override
    public Line with(ITooltipComponent component) {
        components.add(component);
        height = Math.max(component.getHeight(), height);
        return this;
    }

    @Override
    public Line with(Component component) {
        return with(component instanceof DrawableComponent drawable ? drawable : new WrappedComponent(component));
    }

    public int getWidth() {
        if (width == -1) {
            width = components.stream().mapToInt(c -> c.getWidth() + 1).sum();
        }

        return width;
    }

    public int getHeight() {
        return height;
    }

    public void render(PoseStack matrices, int x, int y, float delta) {
        int componentX = x;
        for (ITooltipComponent component : components) {
            int offset = component.getHeight() < height ? (height - component.getHeight()) / 2 : 0;
            component.render(matrices, componentX, y + offset, delta);
            componentX += component.getWidth() + 1;
        }
    }

    // TODO: REMOVE

    @Override
    public MutableComponent setStyle(Style style) {
        return this;
    }

    @Override
    public MutableComponent append(Component component) {
        with(component);
        return this;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY;
    }

    @Override
    public String getContents() {
        return "Line";
    }

    @Override
    public List<Component> getSiblings() {
        return Collections.emptyList();
    }

    @Override
    public MutableComponent plainCopy() {
        return this;
    }

    @Override
    public MutableComponent copy() {
        return this;
    }

    @Override
    public FormattedCharSequence getVisualOrderText() {
        return FormattedCharSequence.EMPTY;
    }

}
