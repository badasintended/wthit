package mcp.mobius.waila.neo;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import mcp.mobius.waila.command.ClientCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class NeoClientCommand extends ClientCommand<CommandSourceStack> {

    public NeoClientCommand() {
        super(new ArgumentBuilderFactory<>() {
            @Override
            public LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
                return Commands.literal(name);
            }

            @Override
            public <T> RequiredArgumentBuilder<CommandSourceStack, T> required(String name, ArgumentType<T> type) {
                return Commands.argument(name, type);
            }
        });
    }

    @Override
    protected FeedbackSender feedback(CommandSourceStack source) {
        return new FeedbackSender() {
            @Override
            public void success(Component message) {
                source.sendSuccess(() -> message, false);
            }

            @Override
            public void fail(Component message) {
                source.sendFailure(message);
            }
        };
    }

}
