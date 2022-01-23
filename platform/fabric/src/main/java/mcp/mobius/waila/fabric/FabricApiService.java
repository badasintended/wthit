package mcp.mobius.waila.fabric;

import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.service.ApiService;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;

public class FabricApiService extends ApiService {

    @Override
    public IModInfo getModInfo(ItemStack stack) {
        return ModInfo.get(Registry.ITEM.getKey(stack.getItem()).getNamespace());
    }

}
