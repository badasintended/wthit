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
import com.google.gson.JsonParser;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.OwnableEntity;

public enum PetOwnerProvider implements IEntityComponentProvider {

    INSTANCE;

    static final Supplier<HttpClient> HTTP = Suppliers.memoize(HttpClient::newHttpClient);

    static final Map<UUID, Future<Component>> NAMES = new HashMap<>();
    static final Component UNKNOWN = Component.literal("???");
    static final Component LOADING = Component.translatable(Tl.Tooltip.Owner.LOADING).withStyle(ChatFormatting.ITALIC);
    static final Component KEY = Component.translatable(Tl.Tooltip.OWNER);

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.PET_OWNER)) {
            OwnableEntity entity = accessor.getEntity();
            var data = accessor.getData().raw();
            var uuid = data.read("owner", UUIDUtil.CODEC).orElse(null);
            if (uuid == null) {
                var owner = entity.getOwner();
                if (owner != null) uuid = owner.getUUID();
            }
            if (uuid == null) return;

            var name = LOADING;
            if (NAMES.containsKey(uuid)) {
                var future = NAMES.get(uuid);
                if (future.isDone()) {
                    name = Futures.getUnchecked(future);
                }
            } else {
                NAMES.put(uuid, requestOwner(uuid));
            }

            if (!(name == UNKNOWN || name == LOADING) || !config.getBoolean(Options.PET_HIDE_UNKNOWN_OWNER)) {
                tooltip.setLine(Options.PET_OWNER, new PairComponent(KEY, name));
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
                var element = JsonParser.parseString(res.body());
                if (element.isJsonObject()) {
                    var object = element.getAsJsonObject();
                    if (object.has("name")) {
                        element = object.get("name");
                        if (element.isJsonPrimitive()) {
                            var nameStr = element.getAsString();
                            if (!nameStr.isBlank()) {
                                return Component.literal(nameStr);
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
