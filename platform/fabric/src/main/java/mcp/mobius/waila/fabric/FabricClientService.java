package mcp.mobius.waila.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.service.IClientService;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class FabricClientService implements IClientService {

    @Override
    public KeyMapping createKeyBind(String id, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(id, InputConstants.Type.KEYSYM, key, WailaClient.KEY_CATEGORY));
    }

}
