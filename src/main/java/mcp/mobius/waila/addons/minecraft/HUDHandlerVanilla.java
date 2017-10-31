package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.addons.HUDHandlerBase;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class HUDHandlerVanilla extends HUDHandlerBase {

    static Block mobSpawner = Blocks.MOB_SPAWNER;
    static Block crops = Blocks.WHEAT;
    static Block beet = Blocks.BEETROOTS;
    static Block farmland = Blocks.FARMLAND;
    static Block melonStem = Blocks.MELON_STEM;
    static Block pumpkinStem = Blocks.PUMPKIN_STEM;
    static Block lever = Blocks.LEVER;
    static Block repeaterIdle = Blocks.UNPOWERED_REPEATER;
    static Block repeaterActv = Blocks.POWERED_REPEATER;
    static Block comparatorIdl = Blocks.UNPOWERED_COMPARATOR;
    static Block comparatorAct = Blocks.POWERED_COMPARATOR;
    static Block redstone = Blocks.REDSTONE_WIRE;
    static Block jukebox = Blocks.JUKEBOX;
    static Block cocoa = Blocks.COCOA;
    static Block netherwart = Blocks.NETHER_WART;
    static Block silverfish = Blocks.MONSTER_EGG;

    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();

        // Disguises silverfish blocks as their normal counterpart
        if (block == silverfish && config.getConfig("vanilla.silverfish")) {
            BlockSilverfish.EnumType type = accessor.getBlockState().getValue(BlockSilverfish.VARIANT);
            return type.getModelBlock().getBlock().getPickBlock(type.getModelBlock(), accessor.getMOP(), accessor.getWorld(), accessor.getPosition(), accessor.getPlayer());
        }

        // Wheat crop should display Wheat item
        if (block == crops)
            return new ItemStack(Items.WHEAT);

        // Beetroot crop should display Beetroot item
        if (block == beet)
            return new ItemStack(Items.BEETROOT);

        // Display farmland instead of dirt
        if (block == farmland)
            return new ItemStack(Blocks.FARMLAND);

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();

        // Adds spawned entity to end of block name
        if (block == mobSpawner && accessor.getTileEntity() instanceof TileEntityMobSpawner && config.getConfig("vanilla.spawntype")) {
            String name = currenttip.get(0);
            String mobname = ((TileEntityMobSpawner) accessor.getTileEntity()).getSpawnerBaseLogic().getCachedEntity().getName();//(accessor.getWorld()).getName();
            currenttip.set(0, String.format("%s (%s)", name, mobname));
        }

        // For some reason, the meta gets added to the tooltip. This removes it.
        if (block == redstone) {
            String name = currenttip.get(0).replaceFirst(String.format(" %s", accessor.getMetadata()), "");
            currenttip.set(0, name);
        }

        // Melon Stem instead of Melon Seeds
        if (block == melonStem)
            currenttip.set(0, TextFormatting.WHITE + I18n.translateToLocal("hud.item.melonstem"));

        // Pumpkin Stem instead of Pumpkin Seeds
        if (block == pumpkinStem)
            currenttip.set(0, TextFormatting.WHITE + I18n.translateToLocal("hud.item.pumpkinstem"));

        return currenttip;
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();

        // Displays maturity percentage
        if (config.getConfig("general.showcrop")) {
            if (BlockCrops.class.isInstance(block))
                addMaturityTooltip(currenttip, accessor.getMetadata() / (float) ((BlockCrops) block).getMaxAge());
            else if (block == melonStem || block == pumpkinStem)
                addMaturityTooltip(currenttip, accessor.getMetadata() / 7F);
            else if (block == cocoa)
                addMaturityTooltip(currenttip, (accessor.getMetadata() >> 2) / 2.0F);
            else if (block == netherwart)
                addMaturityTooltip(currenttip, accessor.getMetadata() / 3.0F);
        }

        // Displays on/off state of Lever
        if (config.getConfig("vanilla.leverstate")) {
            if (block == lever) {
                String redstoneOn = (accessor.getMetadata() & 8) == 0 ? LangUtil.translateG("hud.msg.off") : LangUtil.translateG("hud.msg.on");
                currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.state"), redstoneOn));
                return currenttip;
            }
        }

        // Displays Repeater tick delay
        if (config.getConfig("vanilla.repeater")) {
            if ((block == repeaterIdle) || (block == repeaterActv)) {
                int tick = (accessor.getMetadata() >> 2) + 1;
                if (tick == 1)
                    currenttip.add(String.format("%s : %s tick", LangUtil.translateG("hud.msg.delay"), tick));
                else
                    currenttip.add(String.format("%s : %s ticks", LangUtil.translateG("hud.msg.delay"), tick));
                return currenttip;
            }
        }

        // Displays Comparator output
        if (config.getConfig("vanilla.comparator")) {
            if ((block == comparatorIdl) || (block == comparatorAct)) {
                String mode = ((accessor.getMetadata() >> 2) & 1) == 0 ? LangUtil.translateG("hud.msg.comparator") : LangUtil.translateG("hud.msg.substractor");
                currenttip.add("Mode : " + mode);
                return currenttip;
            }
        }

        // Displays Redstone power
        if (config.getConfig("vanilla.redstone")) {
            if (block == redstone) {
                currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.power"), accessor.getMetadata()));
                return currenttip;
            }
        }

        // Displays playing record
        if (config.getConfig("vanilla.jukebox")) {
            if (block == jukebox) {
                NBTTagCompound tag = accessor.getNBTData();
                if (tag.hasKey("RecordItem", 10)) {
                    Item record = new ItemStack(tag.getCompoundTag("RecordItem")).getItem();
                    currenttip.add(LangUtil.translateG("record.nowPlaying", ((ItemRecord) record).getRecordNameLocal()));
                } else {
                    currenttip.add(LangUtil.translateG("hud.msg.empty"));
                }
            }
        }

        return currenttip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        return te.writeToNBT(tag);
    }

    public static void register(IWailaRegistrar registrar) {
        registrar.addConfig("VanillaMC", "vanilla.spawntype");
        registrar.addConfig("VanillaMC", "vanilla.leverstate");
        registrar.addConfig("VanillaMC", "vanilla.repeater");
        registrar.addConfig("VanillaMC", "vanilla.comparator");
        registrar.addConfig("VanillaMC", "vanilla.redstone");
        registrar.addConfig("VanillaMC", "vanilla.silverfish");
        registrar.addConfigRemote("VanillaMC", "vanilla.jukebox");

        IWailaDataProvider provider = new HUDHandlerVanilla();

        registrar.registerStackProvider(provider, silverfish.getClass());
        registrar.registerStackProvider(provider, crops.getClass());
        registrar.registerStackProvider(provider, beet.getClass());
        registrar.registerStackProvider(provider, farmland.getClass());

        registrar.registerHeadProvider(provider, mobSpawner.getClass());
        registrar.registerHeadProvider(provider, melonStem.getClass());
        registrar.registerHeadProvider(provider, pumpkinStem.getClass());
        registrar.registerHeadProvider(provider, redstone.getClass());

        registrar.registerBodyProvider(provider, BlockCrops.class);
        registrar.registerBodyProvider(provider, melonStem.getClass());
        registrar.registerBodyProvider(provider, pumpkinStem.getClass());
        registrar.registerBodyProvider(provider, cocoa.getClass());
        registrar.registerBodyProvider(provider, netherwart.getClass());
        registrar.registerBodyProvider(provider, lever.getClass());
        registrar.registerBodyProvider(provider, repeaterIdle.getClass());
        registrar.registerBodyProvider(provider, repeaterActv.getClass());
        registrar.registerBodyProvider(provider, comparatorIdl.getClass());
        registrar.registerBodyProvider(provider, comparatorAct.getClass());
        registrar.registerBodyProvider(provider, redstone.getClass());
        registrar.registerBodyProvider(provider, jukebox.getClass());

        registrar.registerNBTProvider(provider, jukebox.getClass());
    }

    private static void addMaturityTooltip(List<String> currentTip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F)
            currentTip.add(LangUtil.translateG("hud.msg.growth") + " : " + LangUtil.translateG("hud.msg.growth.value", (int) growthValue));
        else
            currentTip.add(LangUtil.translateG("hud.msg.growth") + " : " + LangUtil.translateG("hud.msg.mature"));
    }
}
