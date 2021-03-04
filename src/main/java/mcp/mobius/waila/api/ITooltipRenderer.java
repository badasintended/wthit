package mcp.mobius.waila.api;

import java.awt.Dimension;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.CompoundTag;
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
    Dimension getSize(CompoundTag data, ICommonAccessor accessor);

    /**
     * Draw method for the renderer. The GL matrice is automatically moved to the top left of the reserved zone.<br>
     * All calls should be relative to (0,0)
     *
     * @param data     The data supplied by the provider
     * @param accessor A global accessor for TileEntities and Entities
     */
    void draw(MatrixStack matrices, CompoundTag data, ICommonAccessor accessor, int x, int y);

}
