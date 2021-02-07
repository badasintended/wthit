package mcp.mobius.waila.addons.minecraft;

import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITaggableList;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class HUDHandlerVanilla implements IComponentProvider, IServerDataProvider<BlockEntity> {

    static final HUDHandlerVanilla INSTANCE = new HUDHandlerVanilla();

    static final Identifier OBJECT_NAME_TAG = new Identifier(Waila.MODID, "object_name");

    static final ItemStack WHEAT_STACK = new ItemStack(Items.WHEAT);
    static final ItemStack BEETROOT_STACK = new ItemStack(Items.BEETROOT);
    static final ItemStack PLAYER_HEAD_STACK = new ItemStack(Items.PLAYER_HEAD);

    @Override
    public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_HIDE_SILVERFISH) && accessor.getBlock() instanceof InfestedBlock)
            return new ItemStack(((InfestedBlock) accessor.getBlock()).getRegularBlock().asItem());

        if (accessor.getBlock() == Blocks.WHEAT)
            return WHEAT_STACK;

        if (accessor.getBlock() == Blocks.BEETROOTS)
            return BEETROOT_STACK;

        if (accessor.getBlockEntity() instanceof SkullBlockEntity) {
            SkullBlockEntity skull = (SkullBlockEntity) accessor.getBlockEntity();
            if (skull.getOwner() != null) {
                CompoundTag tag = PLAYER_HEAD_STACK.getOrCreateTag();
                CompoundTag skullOwner = tag.getCompound("SkullOwner");
                tag.put("SkullOwner", NbtHelper.fromGameProfile(skullOwner, skull.getOwner()));
                return PLAYER_HEAD_STACK;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void appendHead(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_HIDE_SILVERFISH) && accessor.getBlock() instanceof InfestedBlock)
            ((ITaggableList<Identifier, Text>) tooltip).setTag(OBJECT_NAME_TAG, new LiteralText(String.format(Waila.CONFIG.get().getFormatting().getBlockName(), accessor.getStack().getName().getString())));

        if (accessor.getBlock() == Blocks.SPAWNER && config.get(PluginMinecraft.CONFIG_SPAWNER_TYPE)) {
            MobSpawnerBlockEntity spawner = (MobSpawnerBlockEntity) accessor.getBlockEntity();
            ((ITaggableList<Identifier, Text>) tooltip).setTag(OBJECT_NAME_TAG, new TranslatableText(accessor.getBlock().getTranslationKey())
                .append(new LiteralText(" ("))
                .append(spawner.getLogic().getRenderedEntity(accessor.getWorld()).getDisplayName())
                .append(new LiteralText(")"))
            );
        }
    }

    @Override
    public void appendBody(List<Text> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (config.get(PluginMinecraft.CONFIG_CROP_PROGRESS)) {
            if (accessor.getBlock() instanceof CropBlock) {
                CropBlock crop = (CropBlock) accessor.getBlock();
                addMaturityTooltip(tooltip, accessor.getBlockState().get(crop.getAgeProperty()) / (float) crop.getMaxAge());
            } else if (accessor.getBlock() == Blocks.MELON_STEM || accessor.getBlock() == Blocks.PUMPKIN_STEM) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_7) / 7F);
            } else if (accessor.getBlock() == Blocks.COCOA) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_2) / 2.0F);
            } else if (accessor.getBlock() == Blocks.SWEET_BERRY_BUSH) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_3) / 3.0F);
            } else if (accessor.getBlock() == Blocks.NETHER_WART) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_3) / 3.0F);
            }
        }

        if (config.get(PluginMinecraft.CONFIG_LEVER) && accessor.getBlock() instanceof LeverBlock) {
            boolean active = accessor.getBlockState().get(Properties.POWERED);
            tooltip.add(new TranslatableText("tooltip.waila.state", new TranslatableText("tooltip.waila.state_" + (active ? "on" : "off"))));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_REPEATER) && accessor.getBlock() == Blocks.REPEATER) {
            int delay = accessor.getBlockState().get(Properties.DELAY);
            tooltip.add(new TranslatableText("tooltip.waila.delay", delay));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_COMPARATOR) && accessor.getBlock() == Blocks.COMPARATOR) {
            ComparatorMode mode = accessor.getBlockState().get(Properties.COMPARATOR_MODE);
            tooltip.add(new TranslatableText("tooltip.waila.mode", new TranslatableText("tooltip.waila.mode_" + (mode == ComparatorMode.COMPARE ? "comparator" : "subtractor"))));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_REDSTONE) && accessor.getBlock() == Blocks.REDSTONE_WIRE) {
            tooltip.add(new TranslatableText("tooltip.waila.power", accessor.getBlockState().get(Properties.POWER)));
            return;
        }

        if (config.get(PluginMinecraft.CONFIG_JUKEBOX) && accessor.getBlock() == Blocks.JUKEBOX) {
            if (accessor.getServerData().contains("record"))
                tooltip.add(new TranslatableText("record.nowPlaying", Text.Serializer.fromJson(accessor.getServerData().getString("record"))));
            else
                tooltip.add(new TranslatableText("tooltip.waila.empty"));
        }

        if (config.get(PluginMinecraft.CONFIG_PLAYER_HEAD_NAME) && accessor.getBlockEntity() instanceof SkullBlockEntity) {
            SkullBlockEntity skull = (SkullBlockEntity) accessor.getBlockEntity();
            if (skull.getOwner() != null && !StringUtils.isBlank(skull.getOwner().getName()))
                tooltip.add(new LiteralText(skull.getOwner().getName()));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        if (blockEntity instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukebox = (JukeboxBlockEntity) blockEntity;
            data.putString("record", Text.Serializer.toJson(jukebox.getRecord().toHoverableText()));
        }
    }

    private static void addMaturityTooltip(List<Text> tooltip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F)
            tooltip.add(new TranslatableText("tooltip.waila.crop_growth", String.format("%.0f%%", growthValue)));
        else
            tooltip.add(new TranslatableText("tooltip.waila.crop_growth", new TranslatableText("tooltip.waila.crop_mature")));
    }

}
