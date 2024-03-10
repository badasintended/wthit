package mcp.mobius.waila.fabric;

import java.util.function.Supplier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import mcp.mobius.waila.command.ClientCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class FabricClientCommand extends ClientCommand<FabricClientCommandSource> {

    @Override
    protected LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
        return ClientCommandManager.literal(name);
    }

    @Override
    protected <T> RequiredArgumentBuilder<FabricClientCommandSource, T> argument(String name, ArgumentType<T> type) {
        return ClientCommandManager.argument(name, type);
    }

    @Override
    protected void success(FabricClientCommandSource source, Supplier<Component> msg) {
        source.sendFeedback(msg.get());
    }

    @Override
    protected void fail(FabricClientCommandSource source, Component msg) {
        source.sendError(msg);
    }

}
