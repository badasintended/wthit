package mcp.mobius.waila.gui.hud;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.ITooltipLine;
import mcp.mobius.waila.api.component.GrowingComponent;
import mcp.mobius.waila.api.component.WrappedComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class Line implements ITooltipLine {

    @Nullable
    public final ResourceLocation tag;
    public final List<ITooltipComponent> components = new ArrayList<>();

    private int width = -1;
    private int height;
    private int growingCount = 0;

    public Line(@Nullable ResourceLocation tag) {
        this.tag = tag;
    }

    @Override
    public Line with(ITooltipComponent component) {
        components.add(component);
        height = Math.max(component.getHeight(), height);
        if (component instanceof GrowingComponent) {
            growingCount++;
        }
        return this;
    }

    @Override
    public Line with(Component component) {
        return with(new WrappedComponent(component));
    }

    public int getWidth() {
        if (width == -1) {
            width = components.stream().mapToInt(c -> {
                int width = c.getWidth();
                return width > 0 ? width + 1 : 0;
            }).sum() - 1;
        }

        return width;
    }

    public int getHeight() {
        return height;
    }

    public void render(PoseStack matrices, int x, int y, int maxWidth, float delta) {
        int componentX = x;
        int growingWidth = -1;
        for (ITooltipComponent component : components) {
            if (component instanceof GrowingComponent) {
                if (growingWidth == -1) {
                    growingWidth = (maxWidth - width) / growingCount;
                    if (growingWidth % 2 == 1) {
                        componentX++;
                    }
                }
                componentX += growingWidth;
                continue;
            }

            if (component.getWidth() <= 0) {
                continue;
            }

            int yOffset = component.getHeight() < height ? (height - component.getHeight()) / 2 : 0;
            component.render(matrices, componentX, y + yOffset, delta);
            componentX += component.getWidth() + 1;
        }
    }

}
