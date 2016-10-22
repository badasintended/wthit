package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockFlowerPot.EnumFlowerType;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.world.World;

import java.util.List;

public class HUDHandlerVanilla implements IWailaDataProvider {

    static Block mobSpawner = Blocks.MOB_SPAWNER;
    static Block crops = Blocks.WHEAT;
    static Block beet = Blocks.BEETROOTS;
    static Block farmland = Blocks.FARMLAND;
    static Block melonStem = Blocks.MELON_STEM;
    static Block pumpkinStem = Blocks.PUMPKIN_STEM;
    static Block carrot = Blocks.CARROTS;
    static Block potato = Blocks.POTATOES;
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
    static Block doubleplant = Blocks.DOUBLE_PLANT;
    static Block leave = Blocks.LEAVES;
    static Block leave2 = Blocks.LEAVES2;
    static Block log = Blocks.LOG;
    static Block log2 = Blocks.LOG2;
    static Block quartz = Blocks.QUARTZ_BLOCK;
    static Block sapling = Blocks.SAPLING;
    static Block stickypiston = Blocks.STICKY_PISTON;
    static Block piston = Blocks.PISTON;
    static Block pistonhead = Blocks.PISTON_HEAD;
    static Block stoneslab = Blocks.STONE_SLAB;
    static Block doublestoneslab = Blocks.DOUBLE_STONE_SLAB;
    static Block woodenslab = Blocks.WOODEN_SLAB;
    static Block doublewoodenslab = Blocks.DOUBLE_WOODEN_SLAB;
    static Block flowerpot = Blocks.FLOWER_POT;
    static Block anvil = Blocks.ANVIL;
    static Block stoneslab2 = Blocks.STONE_SLAB2;
    static Block doublestoneslab2 = Blocks.DOUBLE_STONE_SLAB2;


    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();

        if (block == silverfish && config.getConfig("vanilla.silverfish")) {
            int metadata = accessor.getMetadata();
            switch (metadata) {
                case 0:
                    return new ItemStack(Blocks.STONE);
                case 1:
                    return new ItemStack(Blocks.COBBLESTONE);
                case 2:
                    return new ItemStack(Blocks.BRICK_BLOCK);
                default:
                    return null;
            }
        }

        if (block == redstone) {
            return new ItemStack(Items.REDSTONE);
        }

        if (block == doubleplant) {
            IBlockState state = accessor.getBlockState();
            if (state.getValue(BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.UPPER)
                state = accessor.getWorld().getBlockState(accessor.getPosition().down());
            EnumPlantType variant = state.getValue(BlockDoublePlant.VARIANT);
            return new ItemStack(doubleplant, 1, variant.getMeta());
        }

        if (block == flowerpot) {
            int x = accessor.getPosition().getX();
            int y = accessor.getPosition().getY();
            int z = accessor.getPosition().getZ();
            if (!accessor.getWorld().getBlockState(new BlockPos(x, y, z)).getPropertyNames().contains(BlockFlowerPot.CONTENTS))
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

        if (block instanceof BlockRedstoneOre) {
            return new ItemStack(Blocks.REDSTONE_ORE);
        }

        if (block == crops) {
            return new ItemStack(Items.WHEAT);
        }

        if (block == beet) {
            return new ItemStack(Items.BEETROOT);
        }

        if (block == farmland) {
            return new ItemStack(Blocks.FARMLAND);
        }

        if (block == leave || block == leave2 || block == log || block == log2) {
            return new ItemStack(block, 1, accessor.getMetadata() % 4);
        }

        if ((block == quartz) && (accessor.getMetadata() > 2)) {
            return new ItemStack(block, 1, 2);
        }

        if (block == sapling) {
            return new ItemStack(block, 1, accessor.getMetadata() % 8);
        }

        if (block == pistonhead) {
            if (accessor.getMetadata() < 8)
                return new ItemStack(Blocks.PISTON, 1, 0);
            else
                return new ItemStack(Blocks.STICKY_PISTON, 1, 0);
        }

        if (block == stoneslab) {
            return new ItemStack(block, 1, accessor.getMetadata() % 8);
        }

        if (block == doublestoneslab) {
            return new ItemStack(Blocks.STONE_SLAB, 1, accessor.getMetadata() % 8);
        }

        if (block == woodenslab) {
            return new ItemStack(block, 1, accessor.getMetadata() % 8);
        }

        if (block == doublewoodenslab) {
            return new ItemStack(Blocks.WOODEN_SLAB, 1, accessor.getMetadata() % 8);
        }

        if (block == anvil) {
            return new ItemStack(block, 1, accessor.getMetadata() / 4);
        }

        if (block == stoneslab2) {
            return new ItemStack(block, 1, accessor.getMetadata() % 8);
        }

        if (block == doublestoneslab2) {
            return new ItemStack(Blocks.STONE_SLAB2, 1, accessor.getMetadata() % 8);
        }

        return null;

    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();

        //Mob spawner handler
        if (block == mobSpawner && accessor.getTileEntity() instanceof TileEntityMobSpawner && config.getConfig("vanilla.spawntype")) {
            //TODO
            String name = currenttip.get(0);
            String mobname = ((TileEntityMobSpawner) accessor.getTileEntity()).getSpawnerBaseLogic().getCachedEntity().getName();//(accessor.getWorld()).getName();
            currenttip.set(0, String.format("%s (%s)", name, mobname));
        }

        if (block == redstone) {
            String name = currenttip.get(0).replaceFirst(String.format(" %s", accessor.getMetadata()), "");
            currenttip.set(0, name);
        }

        if (block == melonStem) {
            currenttip.set(0, SpecialChars.WHITE + "Melon stem");
        }

        if (block == pumpkinStem) {
            currenttip.set(0, SpecialChars.WHITE + "Pumpkin stem");
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        /* Crops */
        boolean iscrop = crops.getClass().isInstance(block);    //Done to cover all inheriting mods
        if (config.getConfig("general.showcrop"))
            if (iscrop || block == melonStem || block == pumpkinStem || block == carrot || block == potato) {
                float growthValue = (accessor.getMetadata() / 7.0F) * 100.0F;
                if (growthValue < 100.0)
                    currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
                else
                    currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
                return currenttip;
            }

        if (block == cocoa && config.getConfig("general.showcrop")) {

            float growthValue = ((accessor.getMetadata() >> 2) / 2.0F) * 100.0F;
            if (growthValue < 100.0)
                currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
            else
                currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
            return currenttip;
        }

        if (block == netherwart && config.getConfig("general.showcrop")) {
            float growthValue = (accessor.getMetadata() / 3.0F) * 100.0F;
            if (growthValue < 100.0)
                currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
            else
                currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
            return currenttip;
        }

        if (config.getConfig("vanilla.leverstate"))
            if (block == lever) {
                String redstoneOn = (accessor.getMetadata() & 8) == 0 ? LangUtil.translateG("hud.msg.off") : LangUtil.translateG("hud.msg.on");
                currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.state"), redstoneOn));
                return currenttip;
            }

        if (config.getConfig("vanilla.repeater"))
            if ((block == repeaterIdle) || (block == repeaterActv)) {
                int tick = (accessor.getMetadata() >> 2) + 1;
                if (tick == 1)
                    currenttip.add(String.format("%s : %s tick", LangUtil.translateG("hud.msg.delay"), tick));
                else
                    currenttip.add(String.format("%s : %s ticks", LangUtil.translateG("hud.msg.delay"), tick));
                return currenttip;
            }

        if (config.getConfig("vanilla.comparator"))
            if ((block == comparatorIdl) || (block == comparatorAct)) {
                String mode = ((accessor.getMetadata() >> 2) & 1) == 0 ? LangUtil.translateG("hud.msg.comparator") : LangUtil.translateG("hud.msg.substractor");
                //int outputSignal = ((TileEntityComparator)entity).func_96100_a();
                currenttip.add("Mode : " + mode);
                //currenttip.add(String.format("Out : %s", outputSignal));
                return currenttip;
            }

        if (config.getConfig("vanilla.redstone"))
            if (block == redstone) {
                currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.power"), accessor.getMetadata()));
                return currenttip;
            }

        if (config.getConfig("vanilla.jukebox"))
            if (block == jukebox) {
                NBTTagCompound tag = accessor.getNBTData();
                if (tag.hasKey("RecordItem", 10)) {
                    Item record = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("RecordItem")).getItem();
                    currenttip.add(LangUtil.translateG("record.nowPlaying").replace("%s", ((ItemRecord) record).getRecordNameLocal()));
                } else {
                    currenttip.add(LangUtil.translateG("hud.msg.empty"));
                }
            }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te != null)
            te.writeToNBT(tag);
        return tag;
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
        registrar.registerStackProvider(provider, redstone.getClass());
        registrar.registerStackProvider(provider, doubleplant.getClass());
        registrar.registerStackProvider(provider, BlockRedstoneOre.class);
        registrar.registerStackProvider(provider, crops.getClass());
        registrar.registerStackProvider(provider, farmland.getClass());
        registrar.registerStackProvider(provider, leave.getClass());
        registrar.registerStackProvider(provider, leave2.getClass());
        registrar.registerStackProvider(provider, log.getClass());
        registrar.registerStackProvider(provider, log2.getClass());
        registrar.registerStackProvider(provider, quartz.getClass());
        registrar.registerStackProvider(provider, sapling.getClass());
        registrar.registerStackProvider(provider, stickypiston.getClass());
        registrar.registerStackProvider(provider, piston.getClass());
        registrar.registerStackProvider(provider, pistonhead.getClass());
        registrar.registerStackProvider(provider, stoneslab.getClass());
        registrar.registerStackProvider(provider, doublestoneslab.getClass());
        registrar.registerStackProvider(provider, lever.getClass());
        registrar.registerStackProvider(provider, woodenslab.getClass());
        registrar.registerStackProvider(provider, doublewoodenslab.getClass());
        registrar.registerStackProvider(provider, anvil.getClass());
        registrar.registerStackProvider(provider, stoneslab2.getClass());
        registrar.registerStackProvider(provider, doublestoneslab2.getClass());
        registrar.registerStackProvider(provider, flowerpot.getClass());

        //registrar.registerStackProvider(provider, Block.class);
        registrar.registerHeadProvider(provider, mobSpawner.getClass());
        registrar.registerHeadProvider(provider, melonStem.getClass());
        registrar.registerHeadProvider(provider, pumpkinStem.getClass());

        registrar.registerBodyProvider(provider, crops.getClass());
        registrar.registerBodyProvider(provider, melonStem.getClass());
        registrar.registerBodyProvider(provider, pumpkinStem.getClass());
        //registrar.registerBodyProvider(provider, carrot.getClass());
        //registrar.registerBodyProvider(provider, potato.getClass());
        registrar.registerBodyProvider(provider, lever.getClass());
        registrar.registerBodyProvider(provider, repeaterIdle.getClass());
        registrar.registerBodyProvider(provider, repeaterActv.getClass());
        registrar.registerBodyProvider(provider, comparatorIdl.getClass());
        registrar.registerBodyProvider(provider, comparatorAct.getClass());
        registrar.registerHeadProvider(provider, redstone.getClass());
        registrar.registerBodyProvider(provider, redstone.getClass());
        registrar.registerBodyProvider(provider, jukebox.getClass());
        registrar.registerBodyProvider(provider, cocoa.getClass());
        registrar.registerBodyProvider(provider, netherwart.getClass());

        registrar.registerNBTProvider(provider, mobSpawner.getClass());
        registrar.registerNBTProvider(provider, crops.getClass());
        registrar.registerNBTProvider(provider, melonStem.getClass());
        registrar.registerNBTProvider(provider, pumpkinStem.getClass());
        registrar.registerNBTProvider(provider, carrot.getClass());
        registrar.registerNBTProvider(provider, potato.getClass());
        registrar.registerNBTProvider(provider, lever.getClass());
        registrar.registerNBTProvider(provider, repeaterIdle.getClass());
        registrar.registerNBTProvider(provider, repeaterActv.getClass());
        registrar.registerNBTProvider(provider, comparatorIdl.getClass());
        registrar.registerNBTProvider(provider, comparatorAct.getClass());
        registrar.registerNBTProvider(provider, redstone.getClass());
        registrar.registerNBTProvider(provider, jukebox.getClass());
        registrar.registerNBTProvider(provider, cocoa.getClass());
        registrar.registerNBTProvider(provider, netherwart.getClass());
        registrar.registerNBTProvider(provider, silverfish.getClass());

        //registrar.registerDocTextFile("/mcp/mobius/waila/addons/vanillamc/WikiData.csv");

        //ExternalModulesHandler.instance().registerBlockDecorator(new HUDDecoratorVanilla(), repeaterIdle);
    }
}
