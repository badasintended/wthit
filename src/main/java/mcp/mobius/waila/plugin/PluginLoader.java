package mcp.mobius.waila.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginDiscoverer;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.common.s2c.BlacklistSyncCommonS2CPacket;
import mcp.mobius.waila.network.common.s2c.ConfigSyncCommonS2CPacket;
import mcp.mobius.waila.network.common.s2c.PluginSyncCommonS2CPacket;
import mcp.mobius.waila.network.play.c2s.ConfigSyncRequestPlayC2SPacket;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.Log;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public final class PluginLoader {

    private static final Log LOG = Log.create();

    private static boolean discovered = false;

    public static void reloadServer(MinecraftServer server) {
        PluginInfo.refresh();
        load();

        server.getPlayerList().getPlayers().forEach(player -> {
            var sender = PacketSender.s2c(player);
            if (!sender.canSend(PluginSyncCommonS2CPacket.ID)) return;

            if (!server.isSingleplayerOwner(player.getGameProfile())) {
                sender.send(new PluginSyncCommonS2CPacket.Payload());
            }

            sender.send(new BlacklistSyncCommonS2CPacket.Payload());
            sender.send(new ConfigSyncCommonS2CPacket.Payload());
        });
    }

    public static void reloadClient() {
        load();

        if (Minecraft.getInstance().getConnection() != null && PacketSender.c2s().canSend(ConfigSyncRequestPlayC2SPacket.ID)) {
            PacketSender.c2s().send(new ConfigSyncRequestPlayC2SPacket.Payload());
        }
    }

    public static void load() {
        Registrar.destroy();

        if (!discovered) {
            discovered = true;

            for (var discoverer : ServiceLoader.load(IPluginDiscoverer.class)) {
                discoverer.discover((modId, pluginId, side, requiredModIds, defaultEnabled, factory) -> {
                    if (!side.matches(ICommonService.INSTANCE.getSide())) return;
                    if (!requiredModIds.stream().allMatch(it -> ModInfo.get(it).isPresent())) return;
                    PluginInfo.register(discoverer.getDiscovererId(), modId, pluginId, side, requiredModIds, defaultEnabled, factory);
                });
            }
        }

        PluginInfo.saveToggleConfig();
        IPluginInfo extraPlugin = null;

        // TODO: remove legacy method on Minecraft 1.21
        List<String> legacyPlugins = new ArrayList<>();
        for (var info : PluginInfo.getAll()) {
            if (info.getPluginId().equals(Waila.id("extra"))) {
                extraPlugin = info;
            } else {
                initialize(info);
            }

            if (((PluginInfo) info).getDiscoverer().equals(DefaultPluginDiscoverer.LEGACY)) {
                legacyPlugins.add(info.getPluginId().toString());
            }
        }

        if (extraPlugin != null) initialize(extraPlugin);

        if (!legacyPlugins.isEmpty()) {
            LOG.warn("Found plugins registered via legacy platform-dependant method:");
            LOG.warn(legacyPlugins.stream().collect(Collectors.joining(", ", "[", "]")));
            LOG.warn("The method will be removed on Minecraft 1.21");
        }

        Registrar.get().lock();
        PluginConfig.reload();
    }

    private static void initialize(IPluginInfo info) {
        Registrar.get().attach(info);

        if (info.isEnabled()) {
            LOG.info("Initializing plugin {} at {}", info.getPluginId(), info.getInitializer().getClass().getCanonicalName());
        } else {
            LOG.info("Skipping disabled plugin {}", info.getPluginId());
        }

        info.getInitializer().register(Registrar.get());
        Registrar.get().attach(null);
    }

}
