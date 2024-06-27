package mcp.mobius.waila.command;

import java.util.function.Supplier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.mixin.BaseContainerBlockEntityAccess;
import mcp.mobius.waila.network.Packets;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.LockCode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ServerCommand extends CommonCommand<CommandSourceStack, MinecraftServer> {

    public ServerCommand() {
        super(WailaConstants.NAMESPACE);
    }

    @Override
    protected boolean pluginCommandRequirement(CommandSourceStack source) {
        return source.hasPermission(Commands.LEVEL_ADMINS);
    }

    @Override
    protected void register(ArgumentBuilderBuilder<CommandSourceStack> command) {
        command
            .then(Commands.literal("dump"))
            .requires(source -> source.hasPermission(Commands.LEVEL_ADMINS))
            .executes(context -> {
                var source = context.getSource();
                var server = source.getServer();
                var dedicated = server.isDedicatedServer();
                var path = DumpGenerator.generate(dedicated ? DumpGenerator.SERVER : DumpGenerator.LOCAL);
                if (path != null) {
                    Component pathComponent = Component.literal(path.toString()).withStyle(style -> style
                        .withUnderlined(true)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toString())));
                    source.sendSuccess(Component.translatable(dedicated ? Tl.Command.SERVER_DUMP_SUCCESS : Tl.Command.LOCAL_DUMP_SUCCESS, pathComponent), false);
                    var entity = source.getEntity();
                    if (entity instanceof ServerPlayer player && !server.isSingleplayerOwner(player.getGameProfile())) {
                        PacketSender.s2c(player).send(Packets.GENERATE_CLIENT_DUMP, new FriendlyByteBuf(Unpooled.EMPTY_BUFFER));
                    }
                    return 1;
                } else {
                    return 0;
                }
            })
            .pop("dump");

        if (Waila.ENABLE_DEBUG_COMMAND) command
            .then(Commands.literal("debug"))
            .requires(source -> source.hasPermission(Commands.LEVEL_ADMINS))

            .then(Commands.literal("getBlockInfo"))
            .then(Commands.argument("pos", BlockPosArgument.blockPos()))
            .executes(context -> {
                var source = context.getSource();
                var world = source.getLevel();
                var pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                var block = world.getBlockState(pos).getBlock();

                //noinspection deprecation
                source.sendSuccess(Component.literal("Block ID: " + block.builtInRegistryHolder().key().location()), false);
                source.sendSuccess(Component.literal("Block class: " + block.getClass().getName()), false);

                var blockEntity = world.getBlockEntity(pos);
                if (blockEntity != null) {
                    source.sendSuccess(Component.literal("Block entity type ID: " + Registry.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType())), false);
                    source.sendSuccess(Component.literal("Block entity class: " + blockEntity.getClass().getName()), false);
                }

                return 1;
            })
            .pop("pos", "getBlockInfo")

            .then(Commands.literal("getEntityInfo"))
            .then(Commands.argument("target", EntityArgument.entity()))
            .executes(context -> {
                var source = context.getSource();
                var entity = EntityArgument.getEntity(context, "target");

                //noinspection deprecation
                source.sendSuccess(Component.literal("Entity type ID: " + entity.getType().builtInRegistryHolder().key().location()), false);
                source.sendSuccess(Component.literal("Entity class: " + entity.getClass().getName()), false);
                return 1;
            })
            .pop("target", "getEntityInfo")

            .then(Commands.literal("lockContainer"))
            .then(Commands.argument("pos", BlockPosArgument.blockPos()))
            .then(Commands.argument("lock", StringArgumentType.string()))
            .executes(context -> {
                var source = context.getSource();
                var world = source.getLevel();
                var player = source.getPlayer();
                var pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                var lock = StringArgumentType.getString(context, "lock");

                if (player == null) {
                    source.sendFailure(Component.literal("Needs a player"));
                } else if (world.getBlockEntity(pos) instanceof BaseContainerBlockEntityAccess container) {
                    container.wthit_lockKey(new LockCode(lock));
                    var key = new ItemStack(Items.NAME_TAG);
                    key.setHoverName(Component.literal(lock));
                    player.setItemInHand(InteractionHand.MAIN_HAND, key);
                    source.sendSuccess(Component.literal("Locked container " + pos.toShortString() + " with lock \"" + lock + "\""), false);
                    return 1;
                } else {
                    source.sendFailure(Component.literal("Couldn't lock container " + pos.toShortString()));
                }

                return 0;
            })
            .pop("lock", "pos", "lockContainer")

            .pop("debug");
    }

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
        source.sendSuccess(msg.get(), false);
    }

    @Override
    protected void fail(CommandSourceStack source, Component msg) {
        source.sendFailure(msg);
    }

    @Override
    protected MinecraftServer getExecutor(CommandSourceStack source) {
        return source.getServer();
    }

    @Override
    protected void reloadPlugins(MinecraftServer executor) {
        PluginLoader.reloadServerPlugins(executor);
    }

}
