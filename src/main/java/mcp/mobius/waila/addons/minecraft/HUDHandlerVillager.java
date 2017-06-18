package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import javax.annotation.Nonnull;
import java.util.List;

public class HUDHandlerVillager implements IWailaEntityProvider {

    static IWailaEntityProvider INSTANCE = new HUDHandlerVillager();

    @Nonnull
    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        EntityVillager villager = (EntityVillager) entity;
        int careerId = accessor.getNBTData().getInteger("Career") - 1;
        VillagerRegistry.VillagerCareer career = villager.getProfessionForge().getCareer(careerId);
        currenttip.add(I18n.translateToLocalFormatted("hud.msg.career", I18n.translateToLocal("entity.Villager." + career.getName())));
        return currenttip;
    }
}
