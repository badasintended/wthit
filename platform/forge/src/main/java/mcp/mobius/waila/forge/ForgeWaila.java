package mcp.mobius.waila.forge;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.command.DumpCommand;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.impl.Impl;
import mcp.mobius.waila.util.CommonUtil;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
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
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;

@Mod(WailaConstants.WAILA)
@EventBusSubscriber(modid = WailaConstants.WAILA, bus = Bus.MOD)
public class ForgeWaila extends Waila {

    static {
        Impl.reg(IModInfo.class, o -> {
            if (o instanceof String s) {
                return ModInfo.get(s);
            } else if (o instanceof ItemStack i) {
                return ModInfo.get(i.getItem().getCreatorModId(i));
            }
            throw new IllegalArgumentException();
        });
    }

    @SubscribeEvent
    static void setup(FMLCommonSetupEvent event) {
        clientSide = FMLLoader.getDist() == Dist.CLIENT;

        blockBlacklist = BlockTags.createOptional(CommonUtil.id("blacklist"));
        entityBlacklist = EntityTypeTags.createOptional(CommonUtil.id("blacklist"));

        CommonUtil.gameDir = FMLPaths.GAMEDIR.get();
        CommonUtil.configDir = FMLPaths.CONFIGDIR.get();
        init();

        packet = new ForgePacketSender();
        packet.initMain();

        pluginLoader = new ForgePluginLoader();

        ModInfo.supplier = namespace -> ModList.get()
            .getModContainerById(namespace)
            .map(data -> new ModInfo(data.getModId(), data.getModInfo().getDisplayName()));

        String[] mods = {"minecraft", "forge", "wthit", "jei"};
        for (String mod : mods) {
            ModList.get().getModContainerById(mod)
                .map(ModContainer::getModInfo)
                .ifPresent(m -> DumpGenerator.VERSIONS.put(m.getDisplayName(), m.getVersion().toString()));
        }
    }

    @SubscribeEvent
    static void loadComplete(FMLLoadCompleteEvent event) {
        pluginLoader.initialize();
    }

    @EventBusSubscriber(modid = WailaConstants.WAILA)
    static class Subscriber {

        @SubscribeEvent
        static void serverStarting(FMLServerStartingEvent event) {
            PluginConfig.INSTANCE.reload();
        }

        @SubscribeEvent
        static void registerCommands(RegisterCommandsEvent event) {
            DumpCommand.register(event.getDispatcher());
        }

        @SubscribeEvent
        static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            packet.sendBlacklistConfig(blacklistConfig.get(), (ServerPlayer) event.getPlayer());
            packet.sendPluginConfig(PluginConfig.INSTANCE, (ServerPlayer) event.getPlayer());
        }

    }

}
