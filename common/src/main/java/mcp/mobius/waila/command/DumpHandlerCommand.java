package mcp.mobius.waila.command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;
import mcp.mobius.waila.utils.DumpGenerator;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class DumpHandlerCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("wailadumphandler")
            .requires(source -> source.hasPermissionLevel(2))
            .executes(context -> {
                File file = new File("waila_handlers.md");
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(DumpGenerator.generateInfoDump());
                    context.getSource().sendFeedback(new TranslatableText("command.waila.dump_success"), false);
                    return 1;
                } catch (IOException e) {
                    context.getSource().sendError(new LiteralText(e.getClass().getSimpleName() + ": " + e.getMessage()));
                    return 0;
                }
            })
        );
    }

}
