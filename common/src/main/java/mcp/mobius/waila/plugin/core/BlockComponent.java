package mcp.mobius.waila.plugin.core;

import java.util.List;

import com.google.common.base.Strings;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public enum BlockComponent implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendHead(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return;

        Block block = accessor.getBlock();
        String name = accessor.getBlockEntity() != null ? accessor.getServerData().getString("customName") : "";
        if (name.isEmpty()) {
            name = block.getName().getString();
        }

        ((ITaggableList<Identifier, Text>) tooltip).setTag(WailaConstants.OBJECT_NAME_TAG, new LiteralText(String.format(accessor.getBlockNameFormat(), name)));
        if (config.get(WailaConstants.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<Identifier, Text>) tooltip).setTag(WailaConstants.REGISTRY_NAME_TAG, new LiteralText(String.format(accessor.getRegistryNameFormat(), Registry.BLOCK.getId(block))));
    }

    @Override
    public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.get(WailaCore.CONFIG_SHOW_POS)) {
            BlockPos pos = accessor.getPosition();
            tooltip.add(new LiteralText("(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")"));
        }

        if (config.get(WailaCore.CONFIG_SHOW_STATES)) {
            BlockState state = accessor.getBlockState();
            state.getProperties().forEach(p -> {
                Comparable<?> value = state.get(p);
                Text valueText = new LiteralText(value.toString()).setStyle(Style.EMPTY.withColor(p instanceof BooleanProperty ? value == Boolean.TRUE ? Formatting.GREEN : Formatting.RED : Formatting.RESET));
                tooltip.add(new LiteralText(p.getName() + ":").append(valueText));
            });
        }
    }

    @Override
    public void appendTail(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!config.get(WailaConstants.CONFIG_SHOW_MOD_NAME))
            return;

        String modName = ModInfo.get(accessor.getStack().getItem()).name();
        if (!Strings.isNullOrEmpty(modName)) {
            modName = String.format(accessor.getModNameFormat(), modName);
            ((ITaggableList<Identifier, Text>) tooltip).setTag(WailaConstants.MOD_NAME_TAG, new LiteralText(modName));
        }
    }

    @Override
    public void appendServerData(NbtCompound data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        if (blockEntity instanceof Nameable) {
            Text name = ((Nameable) blockEntity).getCustomName();
            if (name != null) {
                data.putString("customName", name.getString());
            }
        }
    }

}
