package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.handlers.VanillaTooltipHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;

import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDHandlerEntities implements IWailaEntityProvider {

    static final IWailaEntityProvider INSTANCE = new HUDHandlerEntities();

    public static int nhearts = 20;
    public static float maxhpfortext = 40.0f;

    @Override
    public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        try {
            currenttip.add(String.format(VanillaTooltipHandler.objectNameWrapper, entity.getName()));
        } catch (Exception e) {
            currenttip.add(String.format(VanillaTooltipHandler.objectNameWrapper, "Unknown"));
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        if (!config.getConfig("general.showhp")) return currenttip;

        if (entity instanceof EntityLivingBase) {
            String hptip = "";

            nhearts = nhearts <= 0 ? 20 : nhearts;

            float health = ((EntityLivingBase) entity).getHealth() / 2.0f;
            float maxhp = ((EntityLivingBase) entity).getMaxHealth() / 2.0f;

            if (((EntityLivingBase) entity).getMaxHealth() > maxhpfortext)
                currenttip.add(String.format("HP : " + WHITE + "%.0f" + GRAY + " / " + WHITE + "%.0f", ((EntityLivingBase) entity).getHealth(), ((EntityLivingBase) entity).getMaxHealth()));

            else {
                currenttip.add(getRenderString("waila.health", String.valueOf(nhearts), String.valueOf(health), String.valueOf(maxhp)));
            }
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        try {
            currenttip.add(String.format(VanillaTooltipHandler.modNameWrapper, getEntityMod(entity)));
        } catch (Exception e) {
            currenttip.add(String.format(VanillaTooltipHandler.modNameWrapper, "Unknown"));
        }
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity te, NBTTagCompound tag, World world) {
        return tag;
    }

    private static String getEntityMod(Entity entity) {
        String modName = "";
        try {
            EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
            ModContainer modC = er.getContainer();
            modName = modC.getName();
        } catch (NullPointerException e) {
            modName = "Minecraft";
        }
        return modName;
    }
}