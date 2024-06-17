package mcp.mobius.waila.api.component;

import java.text.DecimalFormat;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;

/**
 * Component that renders 3D position coordinate.
 */
@ApiSide.ClientOnly
public class PositionComponent extends WrappedComponent {

    private static final DecimalFormat DECIMAL = new DecimalFormat("0.##");

    public PositionComponent(Vec3i pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public PositionComponent(Position pos) {
        this(pos.x(), pos.y(), pos.z());
    }

    public PositionComponent(double x, double y, double z) {
        super(Component.literal("(")
            .append(Component.literal(DECIMAL.format(x)).withStyle(ChatFormatting.RED))
            .append(", ")
            .append(Component.literal(DECIMAL.format(y)).withStyle(ChatFormatting.GREEN))
            .append(", ")
            .append(Component.literal(DECIMAL.format(z)).withStyle(ChatFormatting.BLUE))
            .append(")"));
    }

}
