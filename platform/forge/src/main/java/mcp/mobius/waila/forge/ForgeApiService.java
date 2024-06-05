package mcp.mobius.waila.forge;

import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.service.ApiService;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.world.item.ItemStack;

public class ForgeApiService extends ApiService {

    @Override
    public IModInfo getModInfo(ItemStack stack) {
        return ModInfo.get(stack.getItem().getCreatorModId(stack));
    }

    @Override
    public String getDefaultEnergyUnit() {
        return "FE";
    }

}
