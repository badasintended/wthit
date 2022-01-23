package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface ITooltipLine {

    ITooltipLine with(ITooltipComponent component);

    ITooltipLine with(Component component);

}
