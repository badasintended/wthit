package mcp.mobius.waila.forge;

import mcp.mobius.waila.command.ServerCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;

public class ForgeServerCommand extends ServerCommand {

    @Override
    protected @Nullable String fillContainer(ServerLevel world, BlockPos pos, ServerPlayer player) {
        var be = world.getBlockEntity(pos);
        if (be == null) return "No BlockEntity at " + pos.toShortString();
        var handler = be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).resolve().orElse(null);
        if (handler == null) return "No storage at " + pos.toShortString();
        var offHandStack = player.getOffhandItem();

        var size = handler.getSlots();
        for (var i = 0; i < size; i++) {
            var item = !offHandStack.isEmpty()
                ? offHandStack.getItem()
                : Registry.ITEM.getRandom(world.random).orElseThrow().value();

            handler.insertItem(i, new ItemStack(item, item.getMaxStackSize()), false);
        }

        return null;
    }

}
