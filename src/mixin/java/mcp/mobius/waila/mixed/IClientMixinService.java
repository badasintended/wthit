package mcp.mobius.waila.mixed;

import mcp.mobius.waila.api.__internal__.Internals;
import net.minecraft.client.gui.layouts.GridLayout;

public interface IClientMixinService {

    IClientMixinService INSTANCE = Internals.loadService(IClientMixinService.class);

    void optionsScreenRow(GridLayout.RowHelper rowHelper);

}
