package mcp.mobius.waila.command;

import java.util.Arrays;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.screen.InspectorScreen;
import mcp.mobius.waila.gui.screen.WailaConfigScreen;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.commands.SharedSuggestionProvider.suggest;
import static net.minecraft.commands.SharedSuggestionProvider.suggestResource;

public abstract class ClientCommand<S> extends CommonCommand<S, Minecraft> {

    public ClientCommand() {
        super(WailaConstants.NAMESPACE + "c");
    }

    @Override
    protected boolean pluginCommandRequirement(S source) {
        return !Minecraft.getInstance().hasSingleplayerServer();
    }

    @Override
    protected boolean isPluginDisabledOnServer(PluginInfo plugin) {
        return plugin.isDisabledOnServer();
    }

    @Override
    protected final void register(ArgumentBuilderBuilder<S> command) {
        command
            .then(literal("config"))

            .then(literal("open"))
            .executes(context -> {
                var client = Minecraft.getInstance();
                client.schedule(() -> client.setScreen(new WailaConfigScreen(client.screen)));
                return 1;
            })

            .pop("open")

            .then(literal("plugin"))

            .then(argument("id", ResourceLocationArgument.id()))
            .suggests((context, builder) -> suggestResource(PluginConfig.getAllKeys(), builder))
            .executes(context -> {
                var source = context.getSource();
                var id = context.getArgument("id", ResourceLocation.class);
                ConfigEntry<?> entry = PluginConfig.getEntry(id);
                if (entry == null) {
                    fail(source, Component.translatable(Tl.Command.Config.UNKNOWN_ID, id.toString()));
                    return 0;
                }

                success(source, () -> Component.translatable(Tl.Command.Config.Get.ID, id.toString()));
                success(source, () -> Component.translatable(Tl.Command.Config.Get.SYNCED, entry.isSynced()));
                success(source, () -> Component.translatable(Tl.Command.Config.Get.CURRENT_VALUE, entry.getValue(false).toString()));
                success(source, () -> Component.translatable(Tl.Command.Config.Get.DEFAULT_VALUE, entry.getDefaultValue().toString()));
                if (entry.isServerRequired()) {
                    success(source, () -> Component.translatable(Tl.Command.Config.Get.CLIENT_ONLY_VALUE, entry.getClientOnlyValue().toString()));
                }
                return 1;
            })

            .then(argument("value", StringArgumentType.word()))
            .suggests((context, builder) -> {
                var id = context.getArgument("id", ResourceLocation.class);
                var entry = PluginConfig.getEntry(id);
                if (entry != null) {
                    if (entry.getType().equals(ConfigEntry.BOOLEAN)) {
                        return suggest(new String[]{String.valueOf(!((boolean) entry.getValue(false)))}, builder);
                    } else if (entry.getType().equals(ConfigEntry.ENUM)) {
                        var suggestions = Arrays.stream(entry.getLocalValue().getClass().getEnumConstants())
                            .filter(e -> e != entry.getLocalValue())
                            .map(e -> ((Enum<?>) e).name());
                        return suggest(suggestions, builder);
                    }
                }
                return suggest(new String[0], builder);
            })
            .executes(context -> {
                var source = context.getSource();
                var id = context.getArgument("id", ResourceLocation.class);
                var entry = PluginConfig.getEntry(id);
                if (entry == null) {
                    fail(source, Component.translatable(Tl.Command.Config.UNKNOWN_ID, id.toString()));
                    return 0;
                }

                if (entry.blocksClientEdit() && Minecraft.getInstance().getCurrentServer() != null) {
                    fail(source, Component.translatable(Tl.Command.Config.Set.SYNCED, id.toString()));
                }

                var jsonValue = new JsonPrimitive(context.getArgument("value", String.class));
                try {
                    entry.setLocalValue(entry.getType().parser.apply(jsonValue, entry.getDefaultValue()));
                    success(source, () -> Component.translatable(Tl.Command.Config.Set.SUCCESS, id, entry.getLocalValue().toString()));
                    return 1;
                } catch (Throwable throwable) {
                    fail(source, Component.translatable(Tl.Command.Config.Set.PARSE_FAIL, throwable.getMessage()));
                    throwable.printStackTrace();
                    return 0;
                }
            })

            .pop("value", "id", "plugin", "config")

            .then(literal("overlay"))
            .then(argument("enabled", BoolArgumentType.bool()))
            .suggests((context, builder) -> suggest(new String[]{String.valueOf(!Waila.CONFIG.get().getGeneral().isDisplayTooltip())}, builder))
            .executes(context -> {
                var source = context.getSource();
                var enabled = BoolArgumentType.getBool(context, "enabled");
                Waila.CONFIG.get().getGeneral().setDisplayTooltip(enabled);
                success(source, () -> Component.translatable(enabled ? Tl.Command.Overlay.TRUE : Tl.Command.Overlay.FALSE));
                return enabled ? 1 : 0;
            })

            .pop("enabled", "overlay");

        if (Waila.ENABLE_DEBUG_COMMAND) command
            .then(literal("debug"))

            .then(literal("showComponentBounds"))
            .then(argument("enabled", BoolArgumentType.bool()))
            .suggests((context, builder) -> suggest(new String[]{String.valueOf(!WailaClient.showComponentBounds)}, builder))
            .executes(context -> {
                var source = context.getSource();
                var enabled = BoolArgumentType.getBool(context, "enabled");
                Minecraft.getInstance().execute(() -> WailaClient.showComponentBounds = enabled);
                success(source, () -> Component.literal((enabled ? "En" : "Dis") + "abled component bounds"));
                return enabled ? 1 : 0;
            })
            .pop("enabled", "showComponentBounds")

            .then(literal("showFps"))
            .then(argument("enabled", BoolArgumentType.bool()))
            .suggests((context, builder) -> suggest(new String[]{String.valueOf(!WailaClient.showFps)}, builder))
            .executes(context -> {
                var source = context.getSource();
                var enabled = BoolArgumentType.getBool(context, "enabled");
                Minecraft.getInstance().execute(() -> WailaClient.showFps = enabled);
                success(source, () -> Component.literal((enabled ? "En" : "Dis") + "abled FPS display"));
                return enabled ? 1 : 0;
            })
            .pop("enabled", "showFps")

            .then(literal("inspect"))
            .executes(context -> {
                var client = Minecraft.getInstance();
                client.schedule(() -> client.setScreen(new InspectorScreen()));
                return 1;
            })
            .pop("inspect")

            .pop("debug");
    }

    @Override
    protected Minecraft getExecutor(S source) {
        return Minecraft.getInstance();
    }

    @Override
    protected void reloadPlugins(Minecraft executor) {
        PluginLoader.reloadClientPlugins();
    }

}
