package mcp.mobius.waila.neo;

import com.mojang.blaze3d.platform.InputConstants;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.service.IClientService;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

public class NeoClientService implements IClientService {

    @Override
    public KeyMapping createKeyBind(String id, int key) {
        return new KeyMapping(id, KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, key, WailaConstants.MOD_NAME);
    }

}
