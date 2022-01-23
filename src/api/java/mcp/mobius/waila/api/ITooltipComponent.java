package mcp.mobius.waila.api;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.TextureComponent;
import mcp.mobius.waila.api.component.WrappedComponent;
import org.jetbrains.annotations.ApiStatus;

/**
 * @see ItemComponent
 * @see PairComponent
 * @see TextureComponent
 * @see WrappedComponent
 */
@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
public interface ITooltipComponent {

    int getWidth();

    int getHeight();

    void render(PoseStack matrices, int x, int y, float delta);

}
