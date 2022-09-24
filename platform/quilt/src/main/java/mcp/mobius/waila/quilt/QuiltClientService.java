package mcp.mobius.waila.quilt;

import com.mojang.blaze3d.platform.InputConstants;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.service.IClientService;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class QuiltClientService implements IClientService {

    @Override
    public KeyMapping createKeyBind(String id, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(id, InputConstants.Type.KEYSYM, key, WailaConstants.MOD_NAME));
    }

}
