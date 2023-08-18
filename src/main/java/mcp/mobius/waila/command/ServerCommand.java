package mcp.mobius.waila.command;

import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.network.Packets;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ServerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        new ArgumentBuilderBuilder<>(Commands.literal(WailaConstants.NAMESPACE))
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
                        PacketSender.s2c(player).send(Packets.GENERATE_CLIENT_DUMP, new FriendlyByteBuf(Unpooled.EMPTY_BUFFER));
                    }
                    return 1;
                } else {
                    return 0;
                }
            })

            .pop("dump")

            .register(dispatcher);
    }

}
