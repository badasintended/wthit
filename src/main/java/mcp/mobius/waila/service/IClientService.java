package mcp.mobius.waila.service;

import mcp.mobius.waila.impl.Impl;
import net.minecraft.client.KeyMapping;

public interface IClientService {

    IClientService INSTANCE = Impl.loadService(IClientService.class);

    KeyMapping createKeyBind(String id, int key);

}
