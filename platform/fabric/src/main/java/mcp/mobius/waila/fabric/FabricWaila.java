package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.command.DumpCommand;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.impl.Impl;
import mcp.mobius.waila.util.CommonUtil;
import mcp.mobius.waila.util.ModInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;

public class FabricWaila extends Waila implements ModInitializer {

    static {
        Impl.reg(IModInfo.class, o -> {
            if (o instanceof String s) {
                return ModInfo.get(s);
            } else if (o instanceof ItemStack i) {
                return ModInfo.get(Registry.ITEM.getKey(i.getItem()).getNamespace());
            }
            throw new IllegalArgumentException();
        });
    }

    @Override
    public void onInitialize() {
        clientSide = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

        blockBlacklist = TagRegistry.block(CommonUtil.id("blacklist"));
        entityBlacklist = TagRegistry.entityType(CommonUtil.id("blacklist"));

        CommonUtil.gameDir = FabricLoader.getInstance().getGameDir();
        CommonUtil.configDir = FabricLoader.getInstance().getConfigDir();
        init();

        packet = new FabricPacketSender();
        packet.initMain();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
            DumpCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTING.register(server ->
            PluginConfig.INSTANCE.reload());

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
            packet.sendConfig(PluginConfig.INSTANCE, handler.player));

        ModInfo.register(new ModInfo("c", "Common"));
        ModInfo.supplier = namespace -> FabricLoader.getInstance().getModContainer(namespace)
            .map(data -> new ModInfo(data.getMetadata().getId(), data.getMetadata().getName()));

        pluginLoader = new FabricPluginLoader();
        pluginLoader.initialize();

        String[] mods = {"minecraft", "java", "fabricloader", "fabric", "wthit", "roughlyenoughitems"};
        for (String mod : mods) {
            FabricLoader.getInstance()
                .getModContainer(mod)
                .map(ModContainer::getMetadata)
                .ifPresent(m -> DumpGenerator.VERSIONS.put(m.getName(), m.getVersion().getFriendlyString()));
        }
    }

}
