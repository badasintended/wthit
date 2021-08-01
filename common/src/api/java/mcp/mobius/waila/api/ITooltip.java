package mcp.mobius.waila.api;

import java.util.List;

import mcp.mobius.waila.api.internal.ApiSide;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface ITooltip extends List<Component> {

    void set(ResourceLocation tag, Component component);

    @Nullable
    Component remove(ResourceLocation tag);

    @Nullable
    Component get(ResourceLocation tag);

}
