package mcp.mobius.waila.command;

import java.nio.file.Path;

import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.network.Packets;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class DumpCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wailadump")
            .requires(source -> source.hasPermission(2))
            .executes(context -> {
                CommandSourceStack source = context.getSource();
                Path path = Waila.GAME_DIR.resolve(".waila/WailaServerDump.md");
                if (DumpGenerator.generate(path)) {
                    source.sendSuccess(Component.translatable("command.waila.server_dump_success", path), false);
                    Entity entity = source.getEntity();
                    if (entity instanceof ServerPlayer player) {
                        PacketSender.s2c(player).send(Packets.GENERATE_CLIENT_DUMP, new FriendlyByteBuf(Unpooled.EMPTY_BUFFER));
                    }
                    return 1;
                } else {
                    return 0;
                }
            })
        );
    }

}
