package mcp.mobius.waila.plugin.vanilla.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.Futures;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public enum PetOwnerProvider implements IEntityComponentProvider {

    INSTANCE;

    static final Supplier<HttpClient> HTTP = Suppliers.memoize(HttpClient::newHttpClient);

    static final Map<UUID, Future<Component>> NAMES = new HashMap<>();
    static final Component UNKNOWN = new TextComponent("???");
    static final Component LOADING = new TranslatableComponent("tooltip.waila.owner.loading").withStyle(ChatFormatting.ITALIC);
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

            Component name = LOADING;
            if (NAMES.containsKey(uuid)) {
                Future<Component> future = NAMES.get(uuid);
                if (future.isDone()) {
                    name = Futures.getUnchecked(future);
                }
            } else {
                NAMES.put(uuid, requestOwner(uuid));
            }

            if (!(name == UNKNOWN || name == LOADING) || !config.getBoolean(Options.PET_HIDE_UNKNOWN_OWNER)) {
                tooltip.addLine(new PairComponent(KEY, name));
            }
        }
    }

    private Future<Component> requestOwner(UUID uuid) {
        return HTTP.get().sendAsync(HttpRequest.newBuilder()
                    .uri(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
            .thenApplyAsync(res -> {
                JsonElement element = JsonParser.parseString(res.body());
                if (element.isJsonObject()) {
                    JsonObject object = element.getAsJsonObject();
                    if (object.has("name")) {
                        element = object.get("name");
                        if (element.isJsonPrimitive()) {
                            String nameStr = element.getAsString();
                            if (!nameStr.isBlank()) {
                                return new TextComponent(nameStr);
                            }
                        }
                    }
                }
                return UNKNOWN;
            })
            .handle((component, throwable) -> {
                if (component != null) {
                    return component;
                } else if (throwable != null) {
                    throwable.printStackTrace();
                }

                return UNKNOWN;
            });
    }

}
