package mcp.mobius.waila.mixed;

import java.util.List;

import mcp.mobius.waila.api.__internal__.Internals;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.layouts.GridLayout;

public interface IClientMixinService {

    IClientMixinService INSTANCE = Internals.loadService(IClientMixinService.class);

    void optionsScreenRow(GridLayout.RowHelper rowHelper);

    List<KeyMapping> getWrappedBinds();

}
