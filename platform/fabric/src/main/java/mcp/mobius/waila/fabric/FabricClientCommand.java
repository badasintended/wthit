package mcp.mobius.waila.fabric;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import mcp.mobius.waila.command.ClientCommand;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class FabricClientCommand extends ClientCommand<FabricClientCommandSource> {

    public FabricClientCommand() {
        super(new ArgumentBuilderFactory<>() {
            @Override
            public LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
                return ClientCommandManager.literal(name);
            }

            @Override
            public <T> RequiredArgumentBuilder<FabricClientCommandSource, T> required(String name, ArgumentType<T> type) {
                return ClientCommandManager.argument(name, type);
            }
        });
    }

    @Override
    protected FeedbackSender feedback(FabricClientCommandSource source) {
        return new FeedbackSender() {
            @Override
            public void success(Component message) {
                source.sendFeedback(message);
            }

            @Override
            public void fail(Component message) {
                source.sendError(message);
            }
        };
    }

}
