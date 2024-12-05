package mcp.mobius.waila.fabric;

import mcp.mobius.waila.command.ServerCommand;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class FabricServerCommand extends ServerCommand {

    @Override
    protected @Nullable String fillContainer(ServerLevel world, BlockPos pos, ServerPlayer player) {
        var storage = ItemStorage.SIDED.find(world, pos, Direction.UP);
        if (storage == null) return "No storage at " + pos.toShortString();

        try (var tx = Transaction.openOuter()) {
            while (true) {
                var offHandStack = player.getOffhandItem();
                var item = !offHandStack.isEmpty()
                    ? offHandStack.getItem()
                    : BuiltInRegistries.ITEM.getRandom(world.random).orElseThrow().value();

                if (storage.insert(ItemVariant.of(item), item.getDefaultMaxStackSize(), tx) == 0L) break;
            }
            tx.commit();
            return null;
        }
    }

}
