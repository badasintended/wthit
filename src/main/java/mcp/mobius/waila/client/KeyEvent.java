package mcp.mobius.waila.client;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.gui.screens.config.ScreenConfig;
import mcp.mobius.waila.handlers.JEIHandler;
import mcp.mobius.waila.overlay.RayTracing;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(Side.CLIENT)
public class KeyEvent {
    public static KeyBinding key_cfg;
    public static KeyBinding key_show;
    public static KeyBinding key_liquid;
    public static KeyBinding key_recipe;
    public static KeyBinding key_usage;

    public static void init() {
        KeyEvent.key_cfg = new KeyBinding(Constants.BIND_WAILA_CFG, KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NUMPAD0, "Waila");
        KeyEvent.key_show = new KeyBinding(Constants.BIND_WAILA_SHOW, KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NUMPAD1, "Waila");
        KeyEvent.key_liquid = new KeyBinding(Constants.BIND_WAILA_LIQUID, KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NUMPAD2, "Waila");
        KeyEvent.key_recipe = new KeyBinding(Constants.BIND_WAILA_RECIPE, KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NUMPAD3, "Waila");
        KeyEvent.key_usage = new KeyBinding(Constants.BIND_WAILA_USAGE, KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NUMPAD4, "Waila");

        ClientRegistry.registerKeyBinding(KeyEvent.key_cfg);
        ClientRegistry.registerKeyBinding(KeyEvent.key_show);
        ClientRegistry.registerKeyBinding(KeyEvent.key_liquid);
        ClientRegistry.registerKeyBinding(KeyEvent.key_recipe);
        ClientRegistry.registerKeyBinding(KeyEvent.key_usage);
    }

    @SubscribeEvent
    public static void onKeyEvent(KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (key_cfg.isPressed()) {
            if (mc.currentScreen == null)
                mc.displayGuiScreen(new ScreenConfig(null));
        } else if (key_show.isKeyDown() && ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false)) {
            boolean status = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true);
            ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, !status);
        } else if (key_show.isPressed() && !ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false)) {
            ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true);
        } else if (key_liquid.isPressed()) {
            boolean status = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true);
            ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, !status);
        }

        if (Loader.isModLoaded("jei")) {
            if (key_recipe.isPressed()) {
                ItemStack targetStack = RayTracing.instance().getTargetStack();
                if (!targetStack.isEmpty())
                    JEIHandler.displayRecipes(targetStack);
            } else if (key_usage.isPressed()) {
                ItemStack targetStack = RayTracing.instance().getTargetStack();
                if (!targetStack.isEmpty())
                    JEIHandler.displayUses(targetStack);
            }
        }
    }
}
