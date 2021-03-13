package mcp.mobius.waila;

import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.command.CommandDumpHandlers;
import mcp.mobius.waila.network.NetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(Waila.MODID)
@Mod.EventBusSubscriber(modid = Waila.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WailaForge extends Waila {

    static {
        BLOCK_BLACKLIST = BlockTags.createOptional(new Identifier(MODID, "blacklist"));
        ENTITY_BLACKLIST = EntityTypeTags.createOptional(new Identifier(MODID, "blacklist"));
        CONFIG_DIR = FMLPaths.CONFIGDIR.get();
    }

    @SubscribeEvent
    static void setup(FMLCommonSetupEvent event) {
        Waila.init();
        NetworkHandler.init();
    }

    @SubscribeEvent
    static void loadComplete(FMLLoadCompleteEvent event) {
        WailaPlugins.gatherPlugins();
        WailaPlugins.initializePlugins();
    }

    @Mod.EventBusSubscriber(modid = Waila.MODID, value = Dist.CLIENT)
    static class Subscriber {

        @SubscribeEvent
        static void registerCommands(RegisterCommandsEvent event) {
            CommandDumpHandlers.register(event.getDispatcher());
        }

        @SubscribeEvent
        static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            LOGGER.info("Syncing config to {} ({})", event.getPlayer().getGameProfile().getName(), event.getPlayer().getGameProfile().getId());
            NetworkHandler.sendConfig(PluginConfig.INSTANCE, (ServerPlayerEntity) event.getPlayer());
        }

    }

}
