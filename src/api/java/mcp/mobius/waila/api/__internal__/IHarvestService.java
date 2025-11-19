package mcp.mobius.waila.api.__internal__;

import mcp.mobius.waila.api.IToolType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

/** @hidden */
@ApiStatus.Internal
public interface IHarvestService {

    IHarvestService INSTANCE = Internals.loadService(IHarvestService.class);

    void addToolType(Identifier id, IToolType toolType);

    IToolType.Builder0 createToolTypeBuilder();

    void resetCache();

}
