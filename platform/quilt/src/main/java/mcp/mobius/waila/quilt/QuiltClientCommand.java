package mcp.mobius.waila.quilt;

import java.util.function.Supplier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import mcp.mobius.waila.command.ClientCommand;
import net.minecraft.network.chat.Component;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

public class QuiltClientCommand extends ClientCommand<QuiltClientCommandSource> {

    @Override
    protected LiteralArgumentBuilder<QuiltClientCommandSource> literal(String name) {
        return ClientCommandManager.literal(name);
    }

    @Override
    protected <T> RequiredArgumentBuilder<QuiltClientCommandSource, T> argument(String name, ArgumentType<T> type) {
        return ClientCommandManager.argument(name, type);
    }

    @Override
    protected void success(QuiltClientCommandSource source, Supplier<Component> msg) {
        source.sendFeedback(msg.get());
    }

    @Override
    protected void fail(QuiltClientCommandSource source, Component msg) {
        source.sendError(msg);
    }

}
