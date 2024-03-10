package mcp.mobius.waila.neo;

import java.util.function.Supplier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import mcp.mobius.waila.command.ClientCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class NeoClientCommand extends ClientCommand<CommandSourceStack> {

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
        return Commands.literal(name);
    }

    @Override
    protected <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> type) {
        return Commands.argument(name, type);
    }

    @Override
    protected void success(CommandSourceStack source, Supplier<Component> msg) {
        source.sendSuccess(msg, false);
    }

    @Override
    protected void fail(CommandSourceStack source, Component msg) {
        source.sendFailure(msg);
    }

}
