package mcp.mobius.waila.neo;

import mcp.mobius.waila.command.ServerCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class NeoServerCommand extends ServerCommand {

    @Override
    protected @Nullable String fillContainer(ServerLevel world, BlockPos pos, ServerPlayer player) {
        var handler = world.getCapability(Capabilities.Item.BLOCK, pos, null);
        if (handler == null) return "No storage at " + pos.toShortString();

        try (var tx = Transaction.open(null)) {
            while (true) {
                var offHandStack = player.getOffhandItem();
                var item = !offHandStack.isEmpty()
                    ? offHandStack.getItem()
                    : BuiltInRegistries.ITEM.getRandom(world.random).orElseThrow().value();

                if (handler.insert(ItemResource.of(item), item.getDefaultMaxStackSize(), tx) == 0L) break;
            }
            tx.commit();
            return null;
        }
    }

}
