package mcp.mobius.waila;

import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.GuiConfigHome;
import mcp.mobius.waila.overlay.WailaTickHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Waila.MODID, value = Dist.CLIENT)
public class WailaClient {

    public static KeyBinding openConfig;
    public static KeyBinding showOverlay;
    public static KeyBinding toggleLiquid;

    public static void initClient() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> ((minecraft, screen) -> new GuiConfigHome(screen)));

        openConfig = new KeyBinding("key.waila.config", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_KP_0), Waila.NAME);
        showOverlay = new KeyBinding("key.waila.show_overlay", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_KP_1), Waila.NAME);
        toggleLiquid = new KeyBinding("key.waila.toggle_liquid", KeyConflictContext.IN_GAME, KeyModifier.NONE, InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_KP_2), Waila.NAME);

        ClientRegistry.registerKeyBinding(openConfig);
        ClientRegistry.registerKeyBinding(showOverlay);
        ClientRegistry.registerKeyBinding(toggleLiquid);
    }

    @SubscribeEvent
    public static void onKeyPressed(InputEvent.KeyInputEvent event) {
        if (openConfig == null || showOverlay == null || toggleLiquid == null)
            return;

        while (openConfig.isPressed()) {
            MinecraftClient.getInstance().openScreen(new GuiConfigHome(null));
        }

        while (showOverlay.isPressed()) {
            if (Waila.CONFIG.get().getGeneral().getDisplayMode() == WailaConfig.DisplayMode.TOGGLE) {
                Waila.CONFIG.get().getGeneral().setDisplayTooltip(!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip());
            }
        }

        while (toggleLiquid.isPressed()) {
            PluginConfig.INSTANCE.set(PluginCore.CONFIG_SHOW_FLUID, PluginConfig.INSTANCE.get(PluginCore.CONFIG_SHOW_FLUID));
        }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            WailaTickHandler.instance().renderOverlay(new MatrixStack());
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            WailaTickHandler.instance().tickClient();
    }

}
