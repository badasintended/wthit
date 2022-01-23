package mcp.mobius.waila.command;

import java.nio.file.Path;

import com.mojang.brigadier.CommandDispatcher;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.debug.DumpGenerator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
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
                    source.sendSuccess(new TranslatableComponent("command.waila.server_dump_success", path), false);
                    Entity entity = source.getEntity();
                    if (entity instanceof ServerPlayer player) {
                        Waila.PACKET.generateClientDump(player);
                    }
                    return 1;
                } else {
                    return 0;
                }
            })
        );
    }

}
