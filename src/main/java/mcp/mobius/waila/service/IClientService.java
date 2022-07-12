package mcp.mobius.waila.service;

import mcp.mobius.waila.api.__internal__.Internals;
import net.minecraft.client.KeyMapping;

public interface IClientService {

    IClientService INSTANCE = Internals.loadService(IClientService.class);

    KeyMapping createKeyBind(String id, int key);

}
