package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class HUDHandlerVillager implements IWailaEntityProvider {

    static IWailaEntityProvider INSTANCE = new HUDHandlerVillager();

    @Nonnull
    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        EntityVillager villager = (EntityVillager) entity;
        int careerId = accessor.getNBTData().getInteger("careerId");
        VillagerRegistry.VillagerCareer career = villager.getProfessionForge().getCareer(careerId);
        currenttip.add(LangUtil.translateG("hud.msg.career", LangUtil.translateG("entity.Villager." + career.getName())));
        return currenttip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
        int careerId = ReflectionHelper.getPrivateValue(EntityVillager.class, (EntityVillager) ent, "field_175563_bv", "careerId");
        tag.setInteger("careerId", careerId - 1);
        return tag;
    }
}
