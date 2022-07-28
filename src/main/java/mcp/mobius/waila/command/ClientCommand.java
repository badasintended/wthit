package mcp.mobius.waila.command;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.screen.HomeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.stream.Stream;

import static net.minecraft.commands.SharedSuggestionProvider.suggest;
import static net.minecraft.commands.SharedSuggestionProvider.suggestResource;

public abstract class ClientCommand<S> {

    private final ArgumentBuilderFactory<S> argument;

    protected ClientCommand(ArgumentBuilderFactory<S> argument) {
        this.argument = argument;
    }

    protected abstract FeedbackSender feedback(S source);

    public final void register(CommandDispatcher<S> dispatcher) {
        new ArgumentBuilderBuilder<>(argument.literal(WailaConstants.NAMESPACE + "c"))

            .then(argument.literal("config"))

            .then(argument.literal("open"))
            .executes(context -> {
                Minecraft client = Minecraft.getInstance();
                client.tell(() -> client.setScreen(new HomeScreen(client.screen)));
                return 1;
            })

            .pop("open")

            .then(argument.literal("plugin"))

            .then(argument.required("id", ResourceLocationArgument.id()))
            .suggests((context, builder) -> suggestResource(PluginConfig.getAllKeys(), builder))
            .executes(context -> {
                FeedbackSender feedback = feedback(context.getSource());
                ResourceLocation id = context.getArgument("id", ResourceLocation.class);
                ConfigEntry<?> entry = PluginConfig.getEntry(id);
                if (entry == null) {
                    feedback.fail(Component.translatable("command.waila.config.unknown_id", id));
                    return 0;
                }

                feedback.success(Component.translatable("command.waila.config.get.id", id));
                feedback.success(Component.translatable("command.waila.config.get.synced", entry.isSynced()));
                feedback.success(Component.translatable("command.waila.config.get.current_value", entry.getValue(false).toString()));
                feedback.success(Component.translatable("command.waila.config.get.default_value", entry.getDefaultValue().toString()));
                if (entry.isServerRequired()) {
                    feedback.success(Component.translatable("command.waila.config.get.client_only_value", entry.getClientOnlyValue().toString()));
                }
                return 1;
            })

            .then(argument.required("value", StringArgumentType.word()))
            .suggests((context, builder) -> {
                ResourceLocation id = context.getArgument("id", ResourceLocation.class);
                ConfigEntry<Object> entry = PluginConfig.getEntry(id);
                if (entry != null) {
                    if (entry.getType().equals(ConfigEntry.BOOLEAN)) {
                        return suggest(new String[]{String.valueOf(!((boolean) entry.getValue(false)))}, builder);
                    } else if (entry.getType().equals(ConfigEntry.ENUM)) {
                        Stream<String> suggestions = Arrays.stream(entry.getLocalValue().getClass().getEnumConstants())
                            .filter(e -> e != entry.getLocalValue())
                            .map(e -> ((Enum<?>) e).name());
                        return suggest(suggestions, builder);
                    }
                }
                return suggest(new String[0], builder);
            })
            .executes(context -> {
                FeedbackSender feedback = feedback(context.getSource());
                ResourceLocation id = context.getArgument("id", ResourceLocation.class);
                ConfigEntry<Object> entry = PluginConfig.getEntry(id);
                if (entry == null) {
                    feedback.fail(Component.translatable("command.waila.config.unknown_id", id));
                    return 0;
                }

                if (entry.blocksClientEdit() && Minecraft.getInstance().getCurrentServer() != null) {
                    feedback.fail(Component.translatable("command.waila.config.set.synced", id));
                }

                JsonPrimitive jsonValue = new JsonPrimitive(context.getArgument("value", String.class));
                try {
                    entry.setLocalValue(entry.getType().parser.apply(jsonValue, entry.getDefaultValue()));
                    feedback.success(Component.translatable("command.waila.config.set.success", id, entry.getLocalValue()));
                    return 1;
                } catch (Throwable throwable) {
                    feedback.fail(Component.translatable("command.waila.config.set.parse_fail", throwable.getMessage()));
                    throwable.printStackTrace();
                    return 0;
                }
            })

            .pop("value", "id", "plugin", "config")

            .then(argument.literal("overlay"))
            .then(argument.required("enabled", BoolArgumentType.bool()))
            .suggests((context, builder) -> suggest(new String[]{String.valueOf(!Waila.CONFIG.get().getGeneral().isDisplayTooltip())}, builder))
            .executes(context -> {
                FeedbackSender feedback = feedback(context.getSource());
                boolean enabled = BoolArgumentType.getBool(context, "enabled");
                Waila.CONFIG.get().getGeneral().setDisplayTooltip(enabled);
                feedback.success(Component.translatable("command.waila.overlay." + enabled));
                return enabled ? 1 : 0;
            })

            .pop("enabled", "overlay")

            .register(dispatcher);
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
