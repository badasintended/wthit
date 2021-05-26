package mcp.mobius.waila.forge;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.command.DumpCommand;
import mcp.mobius.waila.util.DumpGenerator;
import mcp.mobius.waila.util.ModIdentification;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(WailaConstants.WAILA)
@EventBusSubscriber(modid = WailaConstants.WAILA, bus = Bus.MOD)
public class ForgeWaila extends Waila {

    @SubscribeEvent
    static void setup(FMLCommonSetupEvent event) {
        blockBlacklist = BlockTags.createOptional(id("blacklist"));
        entityBlacklist = EntityTypeTags.createOptional(id("blacklist"));

        configDir = FMLPaths.CONFIGDIR.get();
        init();

        packet = new ForgePacketSender();
        packet.initMain();

        plugins = new ForgeWailaPlugins();

        ModIdentification.supplier = namespace -> ModList.get()
            .getModContainerById(namespace)
            .map(data -> new ModIdentification.Info(data.getModId(), data.getModInfo().getDisplayName()));

        String[] mods = {"minecraft", "forge", "wthit", "jei"};
        for (String mod : mods) {
            ModList.get().getModContainerById(mod)
                .map(ModContainer::getModInfo)
                .ifPresent(m -> DumpGenerator.VERSIONS.put(m.getDisplayName(), m.getVersion().toString()));
        }
    }

    @SubscribeEvent
    static void loadComplete(FMLLoadCompleteEvent event) {
        plugins.initialize();
    }

    @EventBusSubscriber(modid = WailaConstants.WAILA)
    static class Subscriber {

        @SubscribeEvent
        static void registerCommands(RegisterCommandsEvent event) {
            DumpCommand.register(event.getDispatcher());
        }

        @SubscribeEvent
        static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            packet.sendConfig(PluginConfig.INSTANCE, (ServerPlayerEntity) event.getPlayer());
        }

    }

}
