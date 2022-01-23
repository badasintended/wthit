package mcp.mobius.waila.plugin.vanilla.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public enum PetOwnerProvider implements IEntityComponentProvider {

    INSTANCE;

    static final Map<UUID, Component> NAMES = new HashMap<>();
    static final Component UNKNOWN = new TextComponent("???");

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.PET_OWNER)) {
            Entity entity = accessor.getEntity();
            UUID uuid = null;
            if (entity instanceof AbstractHorse horse) {
                uuid = horse.getOwnerUUID();
            } else if (entity instanceof OwnableEntity ownableEntity) {
                uuid = ownableEntity.getOwnerUUID();
            }

            if (uuid == null) {
                return;
            }

            Component name;
            if (NAMES.containsKey(uuid)) {
                name = NAMES.get(uuid);
            } else {
                GameProfile profile = Minecraft.getInstance().getMinecraftSessionService().fillProfileProperties(new GameProfile(uuid, null), true);
                name = profile.getName() == null ? null : new TextComponent(profile.getName());
                NAMES.put(uuid, name);
            }

            if (name != null) {
                tooltip.addLine(new PairComponent(new TranslatableComponent("tooltip.waila.owner"), name));
            } else if (!config.getBoolean(Options.PET_HIDE_UNKNOWN_OWNER)) {
                tooltip.addLine(new PairComponent(new TranslatableComponent("tooltip.waila.owner"), UNKNOWN));
            }
        }
    }

}
