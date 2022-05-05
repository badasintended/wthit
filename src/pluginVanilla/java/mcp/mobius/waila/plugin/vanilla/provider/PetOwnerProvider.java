package mcp.mobius.waila.plugin.vanilla.provider;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
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
    static final Component KEY = new TranslatableComponent("tooltip.waila.owner");

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
                name = UNKNOWN;

                try {
                    HttpResponse<String> response = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                            .uri(new URI("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid))
                            .GET()
                            .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

                    JsonElement element = JsonParser.parseString(response.body());
                    if (element.isJsonObject()) {
                        JsonObject object = element.getAsJsonObject();
                        if (object.has("name")) {
                            element = object.get("name");
                            if (element.isJsonPrimitive()) {
                                String nameStr = element.getAsString();
                                if (!nameStr.isBlank()) {
                                    name = new TextComponent(nameStr);
                                }
                            }
                        }
                    }
                } catch (URISyntaxException | IOException | JsonParseException | InterruptedException e) {
                    e.printStackTrace();
                }

                NAMES.put(uuid, name);
            }

            if (name != UNKNOWN || !config.getBoolean(Options.PET_HIDE_UNKNOWN_OWNER)) {
                tooltip.addLine(new PairComponent(KEY, name));
            }
        }
    }

}
