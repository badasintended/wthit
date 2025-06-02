package mcp.mobius.waila.command;

import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.PluginInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.commands.SharedSuggestionProvider.suggestResource;

public abstract class CommonCommand<S, E extends Executor> {

    private final String root;

    protected CommonCommand(String root) {
        this.root = root;
    }

    protected abstract void register(ArgumentBuilderBuilder<S> command);

    protected abstract LiteralArgumentBuilder<S> literal(String name);

    protected abstract <T> RequiredArgumentBuilder<S, T> argument(String name, ArgumentType<T> type);

    protected abstract void success(S source, Supplier<Component> msg);

    protected abstract void fail(S source, Component msg);

    protected abstract E getExecutor(S source);

    protected abstract void reloadPlugins(E executor);

    protected abstract boolean pluginCommandRequirement(S source);

    protected boolean isPluginDisabledOnServer(PluginInfo plugin) {
        return false;
    }

    public final void register(CommandDispatcher<S> dispatcher) {
        new ArgumentBuilderBuilder<>(literal(root))
            .then(literal("plugin"))
            .requires(this::pluginCommandRequirement)

            .then(literal("list"))
            .executes(context -> {
                var source = context.getSource();
                return listPlugins(source, true) + listPlugins(source, false);
            })

            .then(literal("enabled"))
            .executes(context -> listPlugins(context.getSource(), true))
            .pop("enabled")

            .then(literal("available"))
            .executes(context -> listPlugins(context.getSource(), false))
            .pop("available")
            .pop("list")

            .then(literal("enable"))
            .then(argument("name", ResourceLocationArgument.id()))
            .suggests(suggestPlugins(false))
            .executes(context -> modifyPlugin(context, true))
            .pop("name", "enable")

            .then(literal("disable"))
            .then(argument("name", ResourceLocationArgument.id()))
            .suggests(suggestPlugins(true))
            .executes(context -> modifyPlugin(context, false))
            .pop("name", "disable")

            .then(literal("reload"))
            .executes(context -> {
                var source = context.getSource();
                var executor = getExecutor(source);

                executor.execute(() -> reloadPlugins(executor));
                success(source, () -> Component.translatable(Tl.Command.Plugin.RELOAD));
                return 1;
            })
            .pop("reload")

            .pop("plugin")

            .then(this::register)
            .register(dispatcher);
    }

    private Stream<PluginInfo> getPlugins(boolean enabled) {
        return PluginInfo.getAll().stream().filter(it -> enabled == it.isEnabled());
    }

    private int listPlugins(S source, boolean enabled) {
        var plugins = getPlugins(enabled).toList();

        if (plugins.isEmpty()) success(source, () -> Component.translatable(
            enabled ? Tl.Command.Plugin.List.Enabled.NONE : Tl.Command.Plugin.List.Available.NONE));
        else success(source, () -> Component.translatable(
            enabled ? Tl.Command.Plugin.List.Enabled.SUCCESS : Tl.Command.Plugin.List.Available.SUCCESS,
            plugins.size(),
            ComponentUtils.formatList(plugins, p -> ComponentUtils.wrapInSquareBrackets(Component.literal(p.getPluginId().toString())).withStyle(s -> s
                .withColor(enabled ? ChatFormatting.GREEN : ChatFormatting.RED)
                .withHoverEvent(new HoverEvent.ShowText(Component.empty().append(p.getModInfo().getName()))))
            )));

        return plugins.size();
    }

    private SuggestionProvider<S> suggestPlugins(boolean enabled) {
        return (context, builder) -> suggestResource(getPlugins(enabled)
            .filter(it -> !it.isLocked() && !isPluginDisabledOnServer(it))
            .map(PluginInfo::getPluginId), builder);
    }

    private int modifyPlugin(CommandContext<S> context, boolean enable) {
        var source = context.getSource();
        var executor = getExecutor(source);

        var id = context.getArgument("name", ResourceLocation.class);
        var name = id.toString();

        var plugin = PluginInfo.get(id);
        if (plugin == null) {
            fail(source, Component.translatable(Tl.Command.Plugin.UNKNOWN, name));
            return 0;
        }

        if (plugin.isLocked()) {
            fail(source, Component.translatable(Tl.Command.Plugin.LOCKED, name));
            return 0;
        }

        if (isPluginDisabledOnServer(plugin)) {
            fail(source, Component.translatable(Tl.Command.Plugin.DISABLED_ON_SERVER, name));
            return 0;
        }

        executor.execute(() -> {
            plugin.setEnabled(enable);
            reloadPlugins(executor);
        });

        success(source, () -> Component.translatable(
            enable ? Tl.Command.Plugin.Modify.ENABLE : Tl.Command.Plugin.Modify.DISABLE,
            name));
        return 1;
    }

}
