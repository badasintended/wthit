package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.*;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityJukebox;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

public class HUDHandlerVanilla implements IComponentProvider, IServerDataProvider<TileEntity> {

    static final HUDHandlerVanilla INSTANCE = new HUDHandlerVanilla();

    @Override
    public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_HIDE_SILVERFISH) && accessor.getBlock() instanceof BlockSilverfish)
            return new ItemStack(((BlockSilverfish) accessor.getBlock()).getMimickedBlock().asItem());

        if (accessor.getBlock() == Blocks.WHEAT)
            return new ItemStack(Items.WHEAT);

        if (accessor.getBlock() == Blocks.BEETROOTS)
            return new ItemStack(Items.BEETROOT);

        return ItemStack.EMPTY;
    }

    @Override
    public void appendHead(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() == Blocks.SPAWNER && config.get(PluginMinecraft.CONFIG_SPAWNER_TYPE)) {
            TileEntityMobSpawner spawner = (TileEntityMobSpawner) accessor.getTileEntity();
            tooltip.set(0, new TextComponentTranslation(accessor.getBlock().getTranslationKey())
                    .appendSibling(new TextComponentString(" ("))
                    .appendSibling(spawner.getSpawnerBaseLogic().getCachedEntity().getDisplayName())
                    .appendSibling(new TextComponentString(")"))
            );
        }
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_CROP_PROGRESS)) {
            if (accessor.getBlock() instanceof BlockCrops) {
                BlockCrops crop = (BlockCrops) accessor.getBlock();
                addMaturityTooltip(tooltip, accessor.getBlockState().get(crop.getAgeProperty()) / (float) crop.getMaxAge());
            } else if (accessor.getBlock() == Blocks.MELON_STEM || accessor.getBlock() == Blocks.PUMPKIN_STEM) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(BlockStateProperties.AGE_0_7) / 7F);
            } else if (accessor.getBlock() == Blocks.COCOA) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(BlockStateProperties.AGE_0_2) / 2.0F);
            }
        }

        if (config.get(PluginMinecraft.CONFIG_LEVER) && accessor.getBlock() instanceof BlockLever) {
            boolean active = accessor.getBlockState().get(BlockStateProperties.POWERED);
            tooltip.add(new TextComponentTranslation("tooltip.waila.state", new TextComponentTranslation("tooltip.waila.state_" + (active ? "on" : "off"))));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_REPEATER) && accessor.getBlock() == Blocks.REPEATER) {
            int delay = accessor.getBlockState().get(BlockStateProperties.DELAY_1_4);
            tooltip.add(new TextComponentTranslation("waila.tooltip.delay", delay));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_COMPARATOR) && accessor.getBlock() == Blocks.COMPARATOR) {
            ComparatorMode mode = accessor.getBlockState().get(BlockStateProperties.COMPARATOR_MODE);
            tooltip.add(new TextComponentTranslation("tooltip.waila.mode", new TextComponentTranslation("tooltip.waila.mode_." + (mode == ComparatorMode.COMPARE ? "comparator" : "subtractor"))));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_REDSTONE) && accessor.getBlock() == Blocks.REDSTONE_WIRE) {
            tooltip.add(new TextComponentTranslation("tooltip.waila.power", accessor.getBlockState().get(BlockStateProperties.POWER_0_15)));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_JUKEBOX) && accessor.getBlock() == Blocks.JUKEBOX) {
            if (accessor.getServerData().contains("record"))
                tooltip.add(new TextComponentTranslation("record.nowPlaying", ITextComponent.Serializer.fromJson(accessor.getServerData().getString("record"))));
            else
                tooltip.add(new TextComponentTranslation("tooltip.waila.empty"));
        }
    }

    @Override
    public void appendServerData(NBTTagCompound data, EntityPlayerMP player, World world, TileEntity blockEntity) {
        if (blockEntity instanceof TileEntityJukebox) {
            TileEntityJukebox jukebox = (TileEntityJukebox) blockEntity;
            data.putString("record", ITextComponent.Serializer.toJson(jukebox.getRecord().getDisplayName()));
        }
    }

    private static void addMaturityTooltip(List<ITextComponent> tooltip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F)
            tooltip.add(new TextComponentTranslation("tooltip.waila.crop_growth", String.format("%.0f%%", growthValue)));
        else
            tooltip.add(new TextComponentTranslation("tooltip.waila.crop_growth", new TextComponentTranslation("tooltip.waila.crop_mature")));
    }
}
