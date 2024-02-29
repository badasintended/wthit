package mcp.mobius.waila.command;

import java.util.Arrays;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.screen.HomeScreen;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.commands.SharedSuggestionProvider.suggest;
import static net.minecraft.commands.SharedSuggestionProvider.suggestResource;

public abstract class ClientCommand<S> {

    private final ArgumentBuilderFactory<S> argument;

    protected ClientCommand(ArgumentBuilderFactory<S> argument) {
        this.argument = argument;
    }

    protected abstract FeedbackSender feedback(S source);

    public final void register(CommandDispatcher<S> dispatcher) {
        var command = new ArgumentBuilderBuilder<>(argument.literal(WailaConstants.NAMESPACE + "c"))
            .then(argument.literal("reload"))
            .executes(context -> {
                Minecraft.getInstance().execute(PluginLoader::reloadClientPlugins);
                return 1;
            })
            .pop("reload")

            .then(argument.literal("config"))

            .then(argument.literal("open"))
            .executes(context -> {
                var client = Minecraft.getInstance();
                client.tell(() -> client.setScreen(new HomeScreen(client.screen)));
                return 1;
            })

            .pop("open")

            .then(argument.literal("plugin"))

            .then(argument.required("id", ResourceLocationArgument.id()))
            .suggests((context, builder) -> suggestResource(PluginConfig.getAllKeys(), builder))
            .executes(context -> {
                var feedback = feedback(context.getSource());
                var id = context.getArgument("id", ResourceLocation.class);
                ConfigEntry<?> entry = PluginConfig.getEntry(id);
                if (entry == null) {
                    feedback.fail(Component.translatable(Tl.Command.Config.UNKNOWN_ID, id));
                    return 0;
                }

                feedback.success(Component.translatable(Tl.Command.Config.Get.ID, id));
                feedback.success(Component.translatable(Tl.Command.Config.Get.SYNCED, entry.isSynced()));
                feedback.success(Component.translatable(Tl.Command.Config.Get.CURRENT_VALUE, entry.getValue(false).toString()));
                feedback.success(Component.translatable(Tl.Command.Config.Get.DEFAULT_VALUE, entry.getDefaultValue().toString()));
                if (entry.isServerRequired()) {
                    feedback.success(Component.translatable(Tl.Command.Config.Get.CLIENT_ONLY_VALUE, entry.getClientOnlyValue().toString()));
                }
                return 1;
            })

            .then(argument.required("value", StringArgumentType.word()))
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
                var feedback = feedback(context.getSource());
                var id = context.getArgument("id", ResourceLocation.class);
                var entry = PluginConfig.getEntry(id);
                if (entry == null) {
                    feedback.fail(Component.translatable(Tl.Command.Config.UNKNOWN_ID, id));
                    return 0;
                }

                if (entry.blocksClientEdit() && Minecraft.getInstance().getCurrentServer() != null) {
                    feedback.fail(Component.translatable(Tl.Command.Config.Set.SYNCED, id));
                }

                var jsonValue = new JsonPrimitive(context.getArgument("value", String.class));
                try {
                    entry.setLocalValue(entry.getType().parser.apply(jsonValue, entry.getDefaultValue()));
                    feedback.success(Component.translatable(Tl.Command.Config.Set.SUCCESS, id, entry.getLocalValue()));
                    return 1;
                } catch (Throwable throwable) {
                    feedback.fail(Component.translatable(Tl.Command.Config.Set.PARSE_FAIL, throwable.getMessage()));
                    throwable.printStackTrace();
                    return 0;
                }
            })

            .pop("value", "id", "plugin", "config")

            .then(argument.literal("overlay"))
            .then(argument.required("enabled", BoolArgumentType.bool()))
            .suggests((context, builder) -> suggest(new String[]{String.valueOf(!Waila.CONFIG.get().getGeneral().isDisplayTooltip())}, builder))
            .executes(context -> {
                var feedback = feedback(context.getSource());
                var enabled = BoolArgumentType.getBool(context, "enabled");
                Waila.CONFIG.get().getGeneral().setDisplayTooltip(enabled);
                feedback.success(Component.translatable(enabled ? Tl.Command.Overlay.TRUE : Tl.Command.Overlay.FALSE));
                return enabled ? 1 : 0;
            })

            .pop("enabled", "overlay");

        if (Waila.ENABLE_DEBUG_COMMAND) command
            .then(argument.literal("debug"))

            .then(argument.literal("showComponentBounds"))
            .then(argument.required("enabled", BoolArgumentType.bool()))
            .suggests((context, builder) -> suggest(new String[]{String.valueOf(!WailaClient.showComponentBounds)}, builder))
            .executes(context -> {
                var feedback = feedback(context.getSource());
                var enabled = BoolArgumentType.getBool(context, "enabled");
                Minecraft.getInstance().execute(() -> WailaClient.showComponentBounds = enabled);
                feedback.success(Component.literal((enabled ? "En" : "Dis") + "abled component bounds"));
                return enabled ? 1 : 0;
            })
            .pop("enabled", "showComponentBounds")

            .then(argument.literal("showFps"))
            .then(argument.required("enabled", BoolArgumentType.bool()))
            .suggests((context, builder) -> suggest(new String[]{String.valueOf(!WailaClient.showFps)}, builder))
            .executes(context -> {
                var feedback = feedback(context.getSource());
                var enabled = BoolArgumentType.getBool(context, "enabled");
                Minecraft.getInstance().execute(() -> WailaClient.showFps = enabled);
                feedback.success(Component.literal((enabled ? "En" : "Dis") + "abled FPS display"));
                return enabled ? 1 : 0;
            })
            .pop("enabled", "showFps")

            .pop("debug");

        command.register(dispatcher);
    }

    protected interface ArgumentBuilderFactory<S> {

        LiteralArgumentBuilder<S> literal(String name);

        <T> RequiredArgumentBuilder<S, T> required(String name, ArgumentType<T> type);

    }

    protected interface FeedbackSender {

        void success(Component message);

        void fail(Component message);

    }


}
