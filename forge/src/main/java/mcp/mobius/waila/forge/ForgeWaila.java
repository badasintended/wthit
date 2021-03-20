package mcp.mobius.waila.forge;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.command.CommandDumpHandlers;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(Waila.MODID)
@Mod.EventBusSubscriber(modid = Waila.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeWaila extends Waila {

    @SubscribeEvent
    static void setup(FMLCommonSetupEvent event) {
        blockBlacklist = BlockTags.createOptional(new Identifier(MODID, "blacklist"));
        entityBlacklist = EntityTypeTags.createOptional(new Identifier(MODID, "blacklist"));
        configDir = FMLPaths.CONFIGDIR.get();

        network = new ForgeNetworkHandler();
        network.main();

        plugins = new ForgeWailaPlugins();

        ModIdentification.supplier = namespace -> ModList.get().getMods().stream()
            .filter(m -> m.getModId().equals(namespace))
            .findFirst()
            .map(data -> new ModIdentification.Info(data.getModId(), data.getDisplayName()));
    }

    @SubscribeEvent
    static void loadComplete(FMLLoadCompleteEvent event) {
        plugins.gatherPlugins();
        plugins.initializePlugins();
    }

    @Mod.EventBusSubscriber(modid = Waila.MODID, value = Dist.CLIENT)
    static class Subscriber {

        @SubscribeEvent
        static void registerCommands(RegisterCommandsEvent event) {
            CommandDumpHandlers.register(event.getDispatcher());
        }

        @SubscribeEvent
        static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            network.sendConfig(PluginConfig.INSTANCE, (ServerPlayerEntity) event.getPlayer());
        }

    }

}
