package mcp.mobius.waila.mixed;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import mcp.mobius.waila.api.__internal__.Internals;
import net.minecraft.client.KeyMapping;

public interface IClientMixinService {

    IClientMixinService INSTANCE = Internals.loadService(IClientMixinService.class);

    List<KeyMapping> getWrappedBinds();

    void saveConfig();

    void onButton(InputConstants.Key key, boolean pressed);

}
