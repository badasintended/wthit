package mcp.mobius.waila.command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;
import mcp.mobius.waila.debug.DumpGenerator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class DumpCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wailadump")
            .requires(source -> source.hasPermission(2))
            .executes(context -> {
                File file = new File("waila_dump.md");
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(DumpGenerator.generateInfoDump());
                    context.getSource().sendSuccess(new TranslatableComponent("command.waila.dump_success"), false);
                    return 1;
                } catch (IOException e) {
                    context.getSource().sendFailure(new TextComponent(e.getClass().getSimpleName() + ": " + e.getMessage()));
                    return 0;
                }
            })
        );
    }

}
