package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;

public class HUDHandlerVillager implements IEntityComponentProvider, IServerDataProvider<Entity> {

    static final HUDHandlerVillager INSTANCE = new HUDHandlerVillager();

    @Override
    public void appendHead(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        tooltip.set(0, new TextComponentString(String.format(Waila.CONFIG.get().getFormatting().getEntityName(), I18n.format("entity.minecraft.villager"))));
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_VILLAGER_PROFESSION) && accessor.getServerData().contains("career")) {
            EntityVillager villager = (EntityVillager) accessor.getEntity();
            int careerId = accessor.getServerData().getInt("career");
            VillagerRegistry.VillagerCareer career = villager.getProfessionForge().getCareer(careerId);
            ITextComponent profession = new TextComponentTranslation("entity.minecraft.villager" + (career.getName().equals("nitwit") ? ".none" : "." + career.getName()));
            tooltip.add(new TextComponentTranslation("tooltip.waila.villager_profession", profession));
        }
    }

    @Override
    public void appendServerData(NBTTagCompound data, EntityPlayerMP player, World world, Entity entity) {
        int careerId = ObfuscationReflectionHelper.getPrivateValue(EntityVillager.class, (EntityVillager) entity, "field_175563_bv");
        data.putInt("career", careerId - 1);
    }
}
