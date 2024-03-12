package mcp.mobius.waila.jaded.w2j;

import java.util.List;
import java.util.function.UnaryOperator;

import mcp.mobius.waila.gui.hud.Line;
import mcp.mobius.waila.gui.hud.Tooltip;
import mcp.mobius.waila.jaded.j2w.JComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.ITooltip;
import snownee.jade.api.ui.Direction2D;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

@SuppressWarnings("NonExtendableApiUsage")
public class WTooltip implements ITooltip {

    public final Tooltip tooltip;

    public WTooltip(mcp.mobius.waila.api.ITooltip tooltip) {
        this.tooltip = (Tooltip) tooltip;
    }

    @Override
    public void clear() {
        tooltip.clear();
    }

    @Override
    public int size() {
        return tooltip.size();
    }

    @Override
    public void add(int i, IElement iElement) {
        tooltip.add(i, new Line(null).with(new JComponent(iElement)));
    }

    @Override
    public void append(int i, IElement iElement) {
        tooltip.getLine(i).with(new JComponent(iElement));
    }

    @Override
    public boolean remove(ResourceLocation resourceLocation) {
         tooltip.setLine(resourceLocation);
         return true;
    }

    @Override
    public boolean replace(ResourceLocation resourceLocation, UnaryOperator<List<List<IElement>>> unaryOperator) {
        return false;
    }

    @Override
    public boolean replace(ResourceLocation resourceLocation, Component component) {
        tooltip.setLine(resourceLocation, component);
        return true;
    }

    @Override
    public IElementHelper getElementHelper() {
        return IElementHelper.get();
    }

    @Override
    public List<IElement> get(ResourceLocation resourceLocation) {
        return List.of(); // TODO
    }

    @Override
    public List<IElement> get(int i, IElement.Align align) {
        return List.of(); // TODO
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public void setLineMargin(int i, Direction2D direction2D, int i1) {

    }

}
