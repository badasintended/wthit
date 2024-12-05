package mcp.mobius.waila.neo;

import mcp.mobius.waila.command.ServerCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public class NeoServerCommand extends ServerCommand {

    @Override
    protected @Nullable String fillContainer(ServerLevel world, BlockPos pos, ServerPlayer player) {
        var handler = world.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if (handler == null) return "No storage at " + pos.toShortString();
        var offHandStack = player.getOffhandItem();

        var size = handler.getSlots();
        for (var i = 0; i < size; i++) {
            var item = !offHandStack.isEmpty()
                ? offHandStack.getItem()
                : BuiltInRegistries.ITEM.getRandom(world.random).orElseThrow().value();

            handler.insertItem(i, new ItemStack(item, item.getDefaultMaxStackSize()), false);
        }

        return null;
    }

}
