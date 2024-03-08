package mcp.mobius.waila.command;

import java.util.stream.Stream;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.mixin.BaseContainerBlockEntityAccess;
import mcp.mobius.waila.network.play.s2c.GenerateClientDumpPlayS2CPacket;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.LockCode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static net.minecraft.commands.SharedSuggestionProvider.suggestResource;

public class ServerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var command = new ArgumentBuilderBuilder<>(Commands.literal(WailaConstants.NAMESPACE))
            .then(Commands.literal("plugin"))
            .requires(source -> source.hasPermission(Commands.LEVEL_ADMINS))

            .then(Commands.literal("list"))
            .executes(context -> {
                var source = context.getSource();
                return listPlugins(source, true) + listPlugins(source, false);
            })

            .then(Commands.literal("enabled"))
            .executes(context -> listPlugins(context.getSource(), true))
            .pop("enabled")

            .then(Commands.literal("available"))
            .executes(context -> listPlugins(context.getSource(), false))
            .pop("available")
            .pop("list")

            .then(Commands.literal("enable"))
            .then(Commands.argument("name", ResourceLocationArgument.id()))
            .suggests(suggestPlugins(false))
            .executes(context -> modifyPlugin(context, true))
            .pop("name", "enable")

            .then(Commands.literal("disable"))
            .then(Commands.argument("name", ResourceLocationArgument.id()))
            .suggests(suggestPlugins(true))
            .executes(context -> modifyPlugin(context, false))
            .pop("name", "disable")

            .pop("plugin")

            .then(Commands.literal("reload"))
            .requires(source -> source.hasPermission(Commands.LEVEL_ADMINS))
            .executes(context -> {
                var source = context.getSource();
                var server = source.getServer();
                server.execute(() -> PluginLoader.reloadServerPlugins(server));
                source.sendSuccess(() -> Component.translatable(Tl.Command.Plugin.RELOAD), false);
                return 1;
            })
            .pop("reload")

            .then(Commands.literal("dump"))
            .requires(source -> source.hasPermission(Commands.LEVEL_ADMINS))
            .executes(context -> {
                var source = context.getSource();
                var server = source.getServer();
                var dedicated = server.isDedicatedServer();
                var path = DumpGenerator.generate(dedicated ? DumpGenerator.SERVER : DumpGenerator.LOCAL);
                if (path != null) {
                    Component pathComponent = Component.literal(path.toString()).withStyle(style -> style
                        .withUnderlined(true)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toString())));
                    source.sendSuccess(() -> Component.translatable(dedicated ? Tl.Command.SERVER_DUMP_SUCCESS : Tl.Command.LOCAL_DUMP_SUCCESS, pathComponent), false);
                    var entity = source.getEntity();
                    if (entity instanceof ServerPlayer player && !server.isSingleplayerOwner(player.getGameProfile())) {
                        PacketSender.s2c(player).send(new GenerateClientDumpPlayS2CPacket.Payload());
                    }
                    return 1;
                } else {
                    return 0;
                }
            })
            .pop("dump");

        if (Waila.ENABLE_DEBUG_COMMAND) command
            .then(Commands.literal("debug"))
            .requires(source -> source.hasPermission(Commands.LEVEL_ADMINS))

            .then(Commands.literal("lockContainer"))
            .then(Commands.argument("pos", BlockPosArgument.blockPos()))
            .then(Commands.argument("lock", StringArgumentType.string()))
            .executes(context -> {
                var source = context.getSource();
                var world = source.getLevel();
                var player = source.getPlayer();
                var pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                var lock = StringArgumentType.getString(context, "lock");

                if (player == null) {
                    source.sendFailure(Component.literal("Needs a player"));
                } else if (world.getBlockEntity(pos) instanceof BaseContainerBlockEntityAccess container) {
                    container.wthit_lockKey(new LockCode(lock));
                    var key = new ItemStack(Items.NAME_TAG);
                    key.setHoverName(Component.literal(lock));
                    player.setItemInHand(InteractionHand.MAIN_HAND, key);
                    source.sendSuccess(() -> Component.literal("Locked container " + pos.toShortString() + " with lock \"" + lock + "\""), false);
                    return 1;
                } else {
                    source.sendFailure(Component.literal("Couldn't lock container " + pos.toShortString()));
                }

                return 0;
            })
            .pop("lock", "pos", "lockContainer")

            .pop("debug");

        command.register(dispatcher);
    }

    private static Stream<IPluginInfo> getPlugins(boolean enabled) {
        return PluginInfo.getAll().stream().filter(it -> enabled == it.isEnabled());
    }

    private static int listPlugins(CommandSourceStack source, boolean enabled) {
        var plugins = getPlugins(enabled).toList();

        if (plugins.isEmpty()) source.sendSuccess(() -> Component.translatable(
            enabled ? Tl.Command.Plugin.List.Enabled.NONE : Tl.Command.Plugin.List.Available.NONE), false);
        else source.sendSuccess(() -> Component.translatable(
            enabled ? Tl.Command.Plugin.List.Enabled.SUCCESS : Tl.Command.Plugin.List.Available.SUCCESS,
            plugins.size(),
            ComponentUtils.formatList(plugins, p -> ComponentUtils.wrapInSquareBrackets(Component.literal(p.getPluginId().toString())).withStyle(s -> s
                .withColor(enabled ? ChatFormatting.GREEN : ChatFormatting.RED)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(p.getModInfo().getName()))))
            )), false);

        return plugins.size();
    }

    private static <S> SuggestionProvider<S> suggestPlugins(boolean enabled) {
        return (context, builder) -> suggestResource(getPlugins(enabled)
            .filter(it -> !((PluginInfo) it).isLocked())
            .map(IPluginInfo::getPluginId), builder);
    }

    private static int modifyPlugin(CommandContext<CommandSourceStack> context, boolean enable) {
        var source = context.getSource();
        var server = source.getServer();

        var name = ResourceLocationArgument.getId(context, "name");
        var plugin = (PluginInfo) PluginInfo.get(name);
        if (plugin == null) {
            source.sendFailure(Component.translatable(Tl.Command.Plugin.UNKNOWN, name));
            return 0;
        }

        if (plugin.isLocked()) {
            source.sendFailure(Component.translatable(Tl.Command.Plugin.LOCKED, name));
            return 0;
        }

        server.execute(() -> {
            plugin.setEnabled(enable);
            PluginLoader.reloadServerPlugins(server);
        });

        source.sendSuccess(() -> Component.translatable(
            enable ? Tl.Command.Plugin.Modify.ENABLE : Tl.Command.Plugin.Modify.DISABLE,
            name), false);
        return 1;
    }

}
