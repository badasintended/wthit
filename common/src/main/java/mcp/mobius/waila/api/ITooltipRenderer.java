package mcp.mobius.waila.api;

import java.awt.Dimension;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Lazy;

public interface ITooltipRenderer {

    /**
     * <b>Please refrain from making a static {@link Dimension} instance since it can cause Minecraft to hang,
     * especially on Mac OS systems due to a LWJGL 3 and AWT incompatibility.
     * Instead, use {@link Lazy}.</b>
     * <pre>{@code
     * static final Lazy<Dimension> DEFAULT = new Lazy(() -> new Dimension(4, 2));
     *
     * @Override
     * Dimension getSize(CompoundTag data, ICommonAccessor accessor) {
     *     return DEFAULT.get();
     * }
     * }</pre>
     *
     * @param data     The data supplied by the provider
     * @param accessor A global accessor for TileEntities and Entities
     *
     * @return Dimension of the reserved area
     */
    Dimension getSize(NbtCompound data, ICommonAccessor accessor);

    /**
     * Draw method for the renderer.
     * TODO: make it not default
     *
     * @param data     The data supplied by the provider
     * @param accessor A global accessor for TileEntities and Entities
     * @param x        The X position of this renderer
     * @param y        The Y position of this renderer
     */
    void draw(MatrixStack matrices, NbtCompound data, ICommonAccessor accessor, int x, int y);

}
