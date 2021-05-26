package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

public enum JukeboxComponent implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.get(WailaVanilla.CONFIG_JUKEBOX)) {
            if (accessor.getServerData().contains("record")) {
                tooltip.add(new TranslatableText("record.nowPlaying", Text.Serializer.fromJson(accessor.getServerData().getString("record"))));
            }
        }
    }

    @Override
    public void appendServerData(NbtCompound data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        if (blockEntity instanceof JukeboxBlockEntity jukebox) {
            ItemStack stack = jukebox.getRecord();
            if (!stack.isEmpty()) {
                Text text = stack.getItem() instanceof MusicDiscItem
                    ? new TranslatableText(stack.getTranslationKey() + ".desc")
                    : stack.getName();
                data.putString("record", Text.Serializer.toJson(text));
            }
        }
    }

}
