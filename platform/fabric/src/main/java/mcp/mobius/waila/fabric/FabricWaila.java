package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.command.DumpCommand;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.impl.Impl;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.CommonUtil;
import mcp.mobius.waila.util.ModInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;

public class FabricWaila extends Waila implements ModInitializer {

    static {
        //noinspection Convert2MethodRef
        Impl.reg(IModInfo.class, (String s) -> ModInfo.get(s));
        Impl.reg(IModInfo.class, (ItemStack i) -> ModInfo.get(Registry.ITEM.getKey(i.getItem()).getNamespace()));
    }

    @Override
    public void onInitialize() {
        clientSide = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

        blockBlacklist = TagFactory.BLOCK.create(CommonUtil.id("blacklist"));
        entityBlacklist = TagFactory.ENTITY_TYPE.create(CommonUtil.id("blacklist"));

        CommonUtil.gameDir = FabricLoader.getInstance().getGameDir();
        CommonUtil.configDir = FabricLoader.getInstance().getConfigDir();
        init();

        packet = new FabricPacketSender();
        packet.initMain();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
            DumpCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTING.register(server ->
            PluginConfig.INSTANCE.reload());

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            packet.sendBlacklistConfig(blacklistConfig.get(), handler.player);
            packet.sendPluginConfig(PluginConfig.INSTANCE, handler.player);
        });

        ModInfo.register(new ModInfo("c", "Common"));
        ModInfo.supplier = namespace -> FabricLoader.getInstance().getModContainer(namespace)
            .map(data -> new ModInfo(data.getMetadata().getId(), data.getMetadata().getName()));

        Registrar.INSTANCE.addEventListener(FabricLegacyEventListener.INSTANCE, 900);

        String[] mods = {"minecraft", "java", "fabricloader", "fabric", "wthit", "roughlyenoughitems"};
        for (String mod : mods) {
            FabricLoader.getInstance()
                .getModContainer(mod)
                .map(ModContainer::getMetadata)
                .ifPresent(m -> DumpGenerator.VERSIONS.put(m.getName(), m.getVersion().getFriendlyString()));
        }

        new FabricPluginLoader().loadPlugins();
    }

}
