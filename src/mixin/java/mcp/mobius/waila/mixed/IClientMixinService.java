package mcp.mobius.waila.mixed;

import java.util.List;

import mcp.mobius.waila.api.__internal__.Internals;
import net.minecraft.client.KeyMapping;

public interface IClientMixinService {

    IClientMixinService INSTANCE = Internals.loadService(IClientMixinService.class);

    List<KeyMapping> getWrappedBinds();

}
