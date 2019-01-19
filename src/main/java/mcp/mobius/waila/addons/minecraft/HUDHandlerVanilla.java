package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class HUDHandlerVanilla implements IComponentProvider, IServerDataProvider<BlockEntity> {

    static final HUDHandlerVanilla INSTANCE = new HUDHandlerVanilla();

    @Override
    public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_HIDE_SILVERFISH) && accessor.getBlock() instanceof StoneInfestedBlock)
            return new ItemStack(((StoneInfestedBlock) accessor.getBlock()).method_10271().getItem()); // getMimicBlock

        if (accessor.getBlock() == Blocks.WHEAT)
            return new ItemStack(Items.WHEAT);

        if (accessor.getBlock() == Blocks.BEETROOTS)
            return new ItemStack(Items.BEETROOT);

        return ItemStack.EMPTY;
    }

    @Override
    public void appendHead(List<TextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() == Blocks.SPAWNER && config.get(PluginMinecraft.CONFIG_SPAWNER_TYPE)) {
            MobSpawnerBlockEntity spawner = (MobSpawnerBlockEntity) accessor.getBlockEntity();
            tooltip.set(0, new TranslatableTextComponent(accessor.getBlock().getTranslationKey())
                    .append(new StringTextComponent(" ("))
                    .append(spawner.getLogic().getRenderedEntity().getDisplayName())
                    .append(new StringTextComponent(")"))
            );
        }
    }

    @Override
    public void appendBody(List<TextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_CROP_PROGRESS)) {
            if (accessor.getBlock() instanceof CropBlock) {
                CropBlock crop = (CropBlock) accessor.getBlock();
                addMaturityTooltip(tooltip, accessor.getBlockState().get(crop.getAgeProperty()) / (float) crop.getCropAgeMaximum());
            } else if (accessor.getBlock() == Blocks.MELON_STEM || accessor.getBlock() == Blocks.PUMPKIN_STEM) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_7) / 7F);
            } else if (accessor.getBlock() == Blocks.COCOA) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_2) / 2.0F);
            } else if (accessor.getBlock() == Blocks.SWEET_BERRY_BUSH) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_3) / 3.0F);
            }
        }

        if (config.get(PluginMinecraft.CONFIG_LEVER) && accessor.getBlock() instanceof LeverBlock) {
            boolean active = accessor.getBlockState().get(Properties.POWERED);
            tooltip.add(new TranslatableTextComponent("tooltip.waila.state", new TranslatableTextComponent("tooltip.waila.state_" + (active ? "on" : "off"))));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_REPEATER) && accessor.getBlock() == Blocks.REPEATER) {
            int delay = accessor.getBlockState().get(Properties.DELAY);
            tooltip.add(new TranslatableTextComponent("waila.tooltip.delay", delay));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_COMPARATOR) && accessor.getBlock() == Blocks.COMPARATOR) {
            ComparatorMode mode = accessor.getBlockState().get(Properties.COMPARATOR_MODE);
            tooltip.add(new TranslatableTextComponent("tooltip.waila.mode", new TranslatableTextComponent("tooltip.waila.mode_." + (mode == ComparatorMode.COMPARE ? "comparator" : "subtractor"))));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_REDSTONE) && accessor.getBlock() == Blocks.REDSTONE_WIRE) {
            tooltip.add(new TranslatableTextComponent("tooltip.waila.power", accessor.getBlockState().get(Properties.POWER)));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_JUKEBOX) && accessor.getBlock() == Blocks.JUKEBOX) {
            if (accessor.getServerData().containsKey("record"))
                tooltip.add(new TranslatableTextComponent("record.nowPlaying", TextComponent.Serializer.fromJsonString(accessor.getServerData().getString("record"))));
            else
                tooltip.add(new TranslatableTextComponent("tooltip.waila.empty"));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        if (blockEntity instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukebox = (JukeboxBlockEntity) blockEntity;
            data.putString("record", TextComponent.Serializer.toJsonString(jukebox.getRecord().getDisplayName()));
        }
    }

    private static void addMaturityTooltip(List<TextComponent> tooltip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F)
            tooltip.add(new TranslatableTextComponent("tooltip.waila.crop_growth", String.format("%.0f%%", growthValue)));
        else
            tooltip.add(new TranslatableTextComponent("tooltip.waila.crop_growth", new TranslatableTextComponent("tooltip.waila.crop_mature")));
    }
}
