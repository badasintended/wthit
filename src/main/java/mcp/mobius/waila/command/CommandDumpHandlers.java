package mcp.mobius.waila.command;

import com.mojang.brigadier.CommandDispatcher;
import mcp.mobius.waila.api.impl.DumpGenerator;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CommandDumpHandlers {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dumpHandlers")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    File file = new File("waila_handlers.md");
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(DumpGenerator.generateInfoDump());
                        context.getSource().sendFeedback(new TranslatableTextComponent("command.waila.dump_success"), false);
                        return 1;
                    } catch (IOException e) {
                        context.getSource().sendError(new StringTextComponent(e.getClass().getSimpleName() + ": " + e.getMessage()));
                        return 0;
                    }
                })
        );
    }
}
