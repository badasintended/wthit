package mcp.mobius.waila.plugin.vanilla.component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public enum PetOwnerComponent implements IEntityComponentProvider {

    INSTANCE;

    static final Map<UUID, GameProfile> GAME_PROFILES = new HashMap<>();

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

            GameProfile profile;
            if (GAME_PROFILES.containsKey(uuid)) {
                profile = GAME_PROFILES.get(uuid);
            } else {
                profile = Minecraft.getInstance().getMinecraftSessionService().fillProfileProperties(new GameProfile(uuid, null), true);
                GAME_PROFILES.put(uuid, profile);
            }

            if (profile.getName() != null) {
                tooltip.add(new TranslatableComponent("tooltip.waila.owner", profile.getName()));
            } else if (!config.getBoolean(Options.PET_HIDE_UNKNOWN_OWNER)) {
                tooltip.add(new TranslatableComponent("tooltip.waila.owner", "???"));
            }
        }
    }

}
