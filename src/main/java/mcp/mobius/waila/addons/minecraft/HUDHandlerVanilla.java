package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.addons.HUDHandlerBase;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.block.*;
import net.minecraft.block.BlockFlowerPot.EnumFlowerType;
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
    static Block flowerpot = Blocks.FLOWER_POT;

    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();

        // Hides whether a block is a Silverfish version or not
        if (block == silverfish && config.getConfig("vanilla.silverfish")) {
            int metadata = accessor.getMetadata();
            switch (metadata) {
                case 0:
                    return new ItemStack(Blocks.STONE);
                case 1:
                    return new ItemStack(Blocks.COBBLESTONE);
                case 2:
                    return new ItemStack(Blocks.STONEBRICK);
                case 3:
                    return new ItemStack(Blocks.STONEBRICK, 1, 1);
                case 4:
                    return new ItemStack(Blocks.STONEBRICK, 1, 2);
                case 5:
                    return new ItemStack(Blocks.STONEBRICK, 1, 3);
                default:
                    return ItemStack.EMPTY;
            }
        }

        // TODO - Find out why this is broken
        // Displays flower type item instead of flowerpot if one exists
        if (block == flowerpot) {
            int x = accessor.getPosition().getX();
            int y = accessor.getPosition().getY();
            int z = accessor.getPosition().getZ();
            if (!accessor.getWorld().getBlockState(new BlockPos(x, y, z)).getPropertyKeys().contains(BlockFlowerPot.CONTENTS))
                return new ItemStack(Blocks.FLOWER_POT, 1, accessor.getMetadata());
            EnumFlowerType variant = accessor.getWorld().getBlockState(new BlockPos(x, y, z)).getValue(BlockFlowerPot.CONTENTS);
            switch (variant) {
                case ACACIA_SAPLING:
                    return new ItemStack(Blocks.SAPLING, 1, 4);
                case ALLIUM:
                    return new ItemStack(Blocks.RED_FLOWER, 1, 2);
                case BIRCH_SAPLING:
                    return new ItemStack(Blocks.SAPLING, 1, 2);
                case BLUE_ORCHID:
                    return new ItemStack(Blocks.RED_FLOWER, 1, 1);
                case CACTUS:
                    return new ItemStack(Blocks.CACTUS, 1, 0);
                case DANDELION:
                    return new ItemStack(Blocks.YELLOW_FLOWER, 1, 0);
                case DARK_OAK_SAPLING:
                    return new ItemStack(Blocks.SAPLING, 1, 5);
                case DEAD_BUSH:
                    return new ItemStack(Blocks.TALLGRASS, 1, 0);
                case EMPTY:
                    return new ItemStack(Items.FLOWER_POT, 1, 0);
                case FERN:
                    return new ItemStack(Blocks.TALLGRASS, 1, 2);
                case HOUSTONIA:
                    return new ItemStack(Blocks.RED_FLOWER, 1, 3);
                case JUNGLE_SAPLING:
                    return new ItemStack(Blocks.SAPLING, 1, 3);
                case MUSHROOM_BROWN:
                    return new ItemStack(Blocks.BROWN_MUSHROOM, 1, 0);
                case MUSHROOM_RED:
                    return new ItemStack(Blocks.RED_MUSHROOM, 1, 0);
                case OAK_SAPLING:
                    return new ItemStack(Blocks.SAPLING, 1, 0);
                case ORANGE_TULIP:
                    return new ItemStack(Blocks.RED_FLOWER, 1, 5);
                case OXEYE_DAISY:
                    return new ItemStack(Blocks.RED_FLOWER, 1, 8);
                case PINK_TULIP:
                    return new ItemStack(Blocks.RED_FLOWER, 1, 7);
                case POPPY:
                    return new ItemStack(Blocks.RED_FLOWER, 1, 0);
                case RED_TULIP:
                    return new ItemStack(Blocks.RED_FLOWER, 1, 4);
                case SPRUCE_SAPLING:
                    return new ItemStack(Blocks.SAPLING, 1, 1);
                case WHITE_TULIP:
                    return new ItemStack(Blocks.RED_FLOWER, 1, 6);
                default:
                    return new ItemStack(Blocks.FLOWER_POT);
            }
        }

        // Wheat crop should display Wheat item
        if (block == crops)
            return new ItemStack(Items.WHEAT);

        // Beetroot crop should display Beetroot item
        if (block == beet)
            return new ItemStack(Items.BEETROOT);

        // Display farmland instead of dirt
        if (block == farmland) {
            return new ItemStack(Blocks.FARMLAND);
        }

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
        if (block == melonStem) {
            currenttip.set(0, TextFormatting.WHITE + I18n.translateToLocal("hud.item.melonstem"));
        }

        // Pumpkin Stem instead of Pumpkin Seeds
        if (block == pumpkinStem) {
            currenttip.set(0, TextFormatting.WHITE + I18n.translateToLocal("hud.item.pumpkinstem"));
        }

        return currenttip;
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();

        // Displays maturity percentage
        if (config.getConfig("general.showcrop") && BlockCrops.class.isInstance(block)) {
            float growthValue = (accessor.getMetadata() / (float) ((BlockCrops) block).getMaxAge()) * 100.0F;
            if (growthValue < 100.0)
                currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
            else
                currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
            return currenttip;
        }

        // Displays maturity percentage
        if (block == melonStem || block == pumpkinStem) {
            float growthValue = (accessor.getMetadata() / 7F) * 100.0F;
            if (growthValue < 100.0)
                currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
            else
                currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
            return currenttip;
        }

        // Displays maturity percentage
        if (block == cocoa && config.getConfig("general.showcrop")) {
            float growthValue = ((accessor.getMetadata() >> 2) / 2.0F) * 100.0F;
            if (growthValue < 100.0)
                currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
            else
                currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
            return currenttip;
        }

        // Displays maturity percentage
        if (block == netherwart && config.getConfig("general.showcrop")) {
            float growthValue = (accessor.getMetadata() / 3.0F) * 100.0F;
            if (growthValue < 100.0)
                currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
            else
                currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
            return currenttip;
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
                //int outputSignal = ((TileEntityComparator)entity).func_96100_a();
                currenttip.add("Mode : " + mode);
                //currenttip.add(String.format("Out : %s", outputSignal));
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
        registrar.registerStackProvider(provider, flowerpot.getClass());
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

        //registrar.registerDocTextFile("/mcp/mobius/waila/addons/vanillamc/WikiData.csv");

        //ExternalModulesHandler.instance().registerBlockDecorator(new HUDDecoratorVanilla(), repeaterIdle);
    }
}
