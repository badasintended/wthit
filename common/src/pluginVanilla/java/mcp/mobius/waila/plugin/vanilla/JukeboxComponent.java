package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

public enum JukeboxComponent implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.JUKEBOX_RECORD)) {
            if (accessor.getServerData().contains("record")) {
                tooltip.add(new TranslatableComponent("record.nowPlaying", Component.Serializer.fromJson(accessor.getServerData().getString("record"))));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity blockEntity) {
        if (blockEntity instanceof JukeboxBlockEntity jukebox) {
            ItemStack stack = jukebox.getRecord();
            if (!stack.isEmpty()) {
                Component text = stack.getItem() instanceof RecordItem
                    ? new TranslatableComponent(stack.getDescriptionId() + ".desc")
                    : stack.getDisplayName();
                data.putString("record", Component.Serializer.toJson(text));
            }
        }
    }

}
