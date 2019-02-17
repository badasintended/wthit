package mcp.mobius.waila;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.command.CommandDumpHandlers;
import mcp.mobius.waila.network.MessageReceiveData;
import mcp.mobius.waila.network.MessageRequestEntity;
import mcp.mobius.waila.network.MessageRequestTile;
import mcp.mobius.waila.network.MessageServerPing;
import mcp.mobius.waila.utils.JsonConfig;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Waila.MODID)
public class Waila {

    public static final String MODID = "waila";
    public static final String NAME = "Waila";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "networking"))
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> "1.0.0")
            .simpleChannel();
    public static final JsonConfig<WailaConfig> CONFIG = new JsonConfig<>(MODID + "/" + MODID, WailaConfig.class)
            .withGson(new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(WailaConfig.ConfigOverlay.ConfigOverlayColor.class, new WailaConfig.ConfigOverlay.ConfigOverlayColor.Adapter())
                    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
                    .create()
            );

    public Waila() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverStarting);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event) {
        NETWORK.registerMessage(0, MessageReceiveData.class, MessageReceiveData::write, MessageReceiveData::read, MessageReceiveData.Handler::onMessage);
        NETWORK.registerMessage(1, MessageServerPing.class, MessageServerPing::write, MessageServerPing::read, MessageServerPing.Handler::onMessage);
        NETWORK.registerMessage(2, MessageRequestEntity.class, MessageRequestEntity::write, MessageRequestEntity::read, MessageRequestEntity.Handler::onMessage);
        NETWORK.registerMessage(3, MessageRequestTile.class, MessageRequestTile::write, MessageRequestTile::read, MessageRequestTile.Handler::onMessage);
    }

    @SubscribeEvent
    public void setupClient(FMLClientSetupEvent event) {
        WailaClient.openConfig = new KeyBinding("key.waila.config", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputMappings.Type.KEYSYM.getOrMakeInput(320), Waila.NAME);
        WailaClient.showOverlay = new KeyBinding("key.waila.show_overlay", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputMappings.Type.KEYSYM.getOrMakeInput(321), Waila.NAME);
        WailaClient.toggleLiquid = new KeyBinding("key.waila.toggle_liquid", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputMappings.Type.KEYSYM.getOrMakeInput(322), Waila.NAME);

        ClientRegistry.registerKeyBinding(WailaClient.openConfig.getKeyBinding());
        ClientRegistry.registerKeyBinding(WailaClient.showOverlay.getKeyBinding());
        ClientRegistry.registerKeyBinding(WailaClient.toggleLiquid.getKeyBinding());
    }

    @SubscribeEvent
    public void loadComplete(FMLLoadCompleteEvent event) {
        new PluginCore().register(WailaRegistrar.INSTANCE);
        ModList.get().getAllScanData().forEach(scan -> {
            scan.getAnnotations().forEach(a -> {
                if (a.getAnnotationType().getClassName().equals(WailaPlugin.class.getName())) {
                    String required = (String) a.getAnnotationData().getOrDefault("value", "");
                    if (required.isEmpty() || ModList.get().isLoaded(required)) {
                        try {
                            Class<?> clazz = Class.forName(a.getMemberName());
                            if (IWailaPlugin.class.isAssignableFrom(clazz)) {
                                IWailaPlugin plugin = (IWailaPlugin) clazz.newInstance();
                                plugin.register(WailaRegistrar.INSTANCE);
                                LOGGER.info("Registered plugin at {}", a.getMemberName());
                            }
                        } catch (Exception e) {
                            LOGGER.error("Error loading plugin at {}", a.getMemberName(), e);
                        }
                    }
                }
            });
        });

        PluginConfig.INSTANCE.reload();
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent event) {
        CommandDumpHandlers.register(event.getCommandDispatcher());
    }

    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        LOGGER.info("Syncing config to {} ({})", event.getPlayer().getGameProfile().getName(), event.getPlayer().getGameProfile().getId());
        NETWORK.sendTo(new MessageServerPing(PluginConfig.INSTANCE), ((EntityPlayerMP) event.getPlayer()).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }
}