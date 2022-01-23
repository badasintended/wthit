package mcp.mobius.waila.api;

import java.awt.Dimension;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated use {@link ITooltipComponent}
 */
@Deprecated
@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
public interface ITooltipRenderer {

    Dimension getSize(CompoundTag data, ICommonAccessor accessor);

    void draw(PoseStack matrices, CompoundTag data, ICommonAccessor accessor, int x, int y);

}
