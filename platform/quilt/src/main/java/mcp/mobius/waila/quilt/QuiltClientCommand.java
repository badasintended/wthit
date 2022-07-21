package mcp.mobius.waila.quilt;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import mcp.mobius.waila.command.ClientCommand;
import net.minecraft.network.chat.Component;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

public class QuiltClientCommand extends ClientCommand<QuiltClientCommandSource> {

    public QuiltClientCommand() {
        super(new ArgumentBuilderFactory<>() {
            @Override
            public LiteralArgumentBuilder<QuiltClientCommandSource> literal(String name) {
                return ClientCommandManager.literal(name);
            }

            @Override
            public <T> RequiredArgumentBuilder<QuiltClientCommandSource, T> required(String name, ArgumentType<T> type) {
                return ClientCommandManager.argument(name, type);
            }
        });
    }

    @Override
    protected FeedbackSender feedback(QuiltClientCommandSource source) {
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
