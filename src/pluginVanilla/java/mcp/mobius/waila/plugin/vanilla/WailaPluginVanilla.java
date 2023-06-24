package mcp.mobius.waila.plugin.vanilla;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.plugin.vanilla.config.DefaultRollbackBlacklistConfig;
import mcp.mobius.waila.plugin.vanilla.config.NoteDisplayMode;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.fluid.LavaDescriptor;
import mcp.mobius.waila.plugin.vanilla.fluid.WaterDescriptor;
import mcp.mobius.waila.plugin.vanilla.provider.BaseContainerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BeehiveProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BlockAttributesProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BoatProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BreakProgressProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ComposterProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ContainerEntityProvider;
import mcp.mobius.waila.plugin.vanilla.provider.EnderChestProvider;
import mcp.mobius.waila.plugin.vanilla.provider.EntityAttributesProvider;
import mcp.mobius.waila.plugin.vanilla.provider.FallingBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.FurnaceProvider;
import mcp.mobius.waila.plugin.vanilla.provider.HopperContainerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.HorseProvider;
import mcp.mobius.waila.plugin.vanilla.provider.InfestedBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.InvisibleEntityProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ItemEntityProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ItemFrameProvider;
import mcp.mobius.waila.plugin.vanilla.provider.JukeboxProvider;
import mcp.mobius.waila.plugin.vanilla.provider.MobTimerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.NoteBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PetOwnerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PlantProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PlayerHeadProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PowderSnowProvider;
import mcp.mobius.waila.plugin.vanilla.provider.RandomizableContainerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.RedstoneProvider;
import mcp.mobius.waila.plugin.vanilla.provider.SpawnerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.TrappedChestProvider;
import mcp.mobius.waila.plugin.vanilla.provider.VehicleProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.material.Fluids;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class WailaPluginVanilla implements IWailaPlugin {

    private IJsonConfig<DefaultRollbackBlacklistConfig> defaultRollbackBlacklistConfig;

    @Override
    public void register(IRegistrar registrar) {
        registrar.addMergedConfig(Options.ITEM_ENTITY, true);
        registrar.addIcon(ItemEntityProvider.INSTANCE, ItemEntity.class);
        registrar.addComponent(ItemEntityProvider.INSTANCE, HEAD, ItemEntity.class, 950);
        registrar.addComponent(ItemEntityProvider.INSTANCE, TAIL, ItemEntity.class, 950);
        registrar.addOverride(ItemEntityProvider.INSTANCE, ItemEntity.class);

        registrar.addMergedSyncedConfig(Options.PET_OWNER, true, false);
        registrar.addConfig(Options.PET_HIDE_UNKNOWN_OWNER, false);
        registrar.addComponent(PetOwnerProvider.INSTANCE, BODY, OwnableEntity.class);
        registrar.addEntityData(PetOwnerProvider.INSTANCE, OwnableEntity.class);

        registrar.addConfig(Options.ATTRIBUTE_BLOCK_POSITION, false);
        registrar.addConfig(Options.ATTRIBUTE_BLOCK_STATE, false);
        registrar.addConfig(Options.ATTRIBUTE_ENTITY_POSITION, false);
        registrar.addMergedConfig(Options.ATTRIBUTE_HEALTH, true);
        registrar.addMergedSyncedConfig(Options.ATTRIBUTE_ABSORPTION, true, false);
        registrar.addMergedConfig(Options.ATTRIBUTE_ARMOR, true);
        registrar.addConfig(Options.ATTRIBUTE_COMPACT, false);
        registrar.addConfig(Options.ATTRIBUTE_ICON_PER_LINE, 25);
        registrar.addConfig(Options.ATTRIBUTE_LONG_HEALTH_MAX, 100);
        registrar.addConfig(Options.ATTRIBUTE_LONG_ARMOR_MAX, 100);
        registrar.addMergedConfig(Options.ATTRUBUTE_HORSE_JUMP_HEIGHT, true);
        registrar.addMergedConfig(Options.ATTRUBUTE_HORSE_SPEED, true);
        registrar.addComponent(BlockAttributesProvider.INSTANCE, BODY, Block.class, 950);
        registrar.addComponent(EntityAttributesProvider.INSTANCE, HEAD, Entity.class, 950);
        registrar.addComponent(EntityAttributesProvider.INSTANCE, BODY, Entity.class, 950);
        registrar.addComponent(HorseProvider.INSTANCE, BODY, AbstractHorse.class);
        registrar.addEntityData(EntityAttributesProvider.INSTANCE, Entity.class);

        registrar.addMergedSyncedConfig(Options.JUKEBOX_RECORD, true, false);
        registrar.addComponent(JukeboxProvider.INSTANCE, BODY, JukeboxBlockEntity.class);
        registrar.addBlockData(JukeboxProvider.INSTANCE, JukeboxBlockEntity.class);

        registrar.addMergedSyncedConfig(Options.TIMER_GROW, true, false);
        registrar.addMergedSyncedConfig(Options.TIMER_BREED, true, false);
        registrar.addComponent(MobTimerProvider.INSTANCE, BODY, AgeableMob.class);
        registrar.addEntityData(MobTimerProvider.INSTANCE, AgeableMob.class);

        registrar.addMergedConfig(Options.OVERRIDE_INVISIBLE_ENTITY, true);
        registrar.addMergedConfig(Options.OVERRIDE_TRAPPED_CHEST, true);
        registrar.addMergedConfig(Options.OVERRIDE_POWDER_SNOW, true);
        registrar.addMergedConfig(Options.OVERRIDE_INFESTED, true);
        registrar.addConfig(Options.OVERRIDE_VEHICLE, true);
        registrar.addOverride(InvisibleEntityProvider.INSTANCE, LivingEntity.class);
        registrar.addOverride(InfestedBlockProvider.INSTANCE, InfestedBlock.class);
        registrar.addOverride(TrappedChestProvider.INSTANCE, TrappedChestBlock.class);
        registrar.addOverride(PowderSnowProvider.INSTANCE, PowderSnowBlock.class);
        registrar.addOverride(VehicleProvider.INSTANCE, Entity.class, 900);

        registrar.addMergedConfig(Options.BREAKING_PROGRESS, true);
        registrar.addConfig(Options.BREAKING_PROGRESS_COLOR, 0xAAFFFFFF, IntFormat.ARGB_HEX);
        registrar.addConfig(Options.BREAKING_PROGRESS_BOTTOM_ONLY, false);
        registrar.addEventListener(BreakProgressProvider.INSTANCE);

        registrar.addMergedConfig(Options.SPAWNER_TYPE, true);
        registrar.addComponent(SpawnerProvider.INSTANCE, HEAD, SpawnerBlockEntity.class, 950);

        registrar.addMergedConfig(Options.CROP_PROGRESS, true);
        registrar.addIcon(PlantProvider.INSTANCE, CropBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, CropBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, StemBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, CocoaBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, NetherWartBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, SweetBerryBushBlock.class);

        registrar.addMergedConfig(Options.REDSTONE_LEVER, true);
        registrar.addMergedConfig(Options.REDSTONE_REPEATER, true);
        registrar.addMergedConfig(Options.REDSTONE_COMPARATOR, true);
        registrar.addMergedConfig(Options.REDSTONE_LEVEL, true);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, LeverBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, RepeaterBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, ComparatorBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, RedStoneWireBlock.class);

        registrar.addMergedConfig(Options.PLAYER_HEAD_NAME, true);
        registrar.addIcon(PlayerHeadProvider.INSTANCE, SkullBlockEntity.class);
        registrar.addComponent(PlayerHeadProvider.INSTANCE, BODY, SkullBlockEntity.class);

        registrar.addMergedConfig(Options.LEVEL_COMPOSTER, true);
        registrar.addMergedConfig(Options.LEVEL_HONEY, true);
        registrar.addComponent(ComposterProvider.INSTANCE, BODY, ComposterBlock.class);
        registrar.addComponent(BeehiveProvider.INSTANCE, BODY, BeehiveBlock.class);

        registrar.addMergedConfig(Options.NOTE_BLOCK_TYPE, true);
        registrar.addConfig(Options.NOTE_BLOCK_NOTE, NoteDisplayMode.SHARP);
        registrar.addConfig(Options.NOTE_BLOCK_INT_VALUE, false);
        registrar.addComponent(NoteBlockProvider.INSTANCE, BODY, NoteBlock.class);

        registrar.addIcon(FallingBlockProvider.INSTANCE, FallingBlockEntity.class);
        registrar.addComponent(FallingBlockProvider.INSTANCE, HEAD, FallingBlockEntity.class);

        registrar.addComponent(BoatProvider.INSTANCE, HEAD, Boat.class, 950);
        registrar.addComponent(BoatProvider.INSTANCE, TAIL, Boat.class, 950);

        registrar.addIcon(ItemFrameProvider.INSTANCE, ItemFrame.class);
        registrar.addComponent(ItemFrameProvider.INSTANCE, HEAD, ItemFrame.class);
        registrar.addComponent(ItemFrameProvider.INSTANCE, TAIL, ItemFrame.class);

        FluidData.describeFluid(Fluids.WATER, WaterDescriptor.INSTANCE);
        FluidData.describeFluid(Fluids.LAVA, LavaDescriptor.INSTANCE);
        FluidData.describeCauldron(Blocks.WATER_CAULDRON, WaterDescriptor.INSTANCE);
        FluidData.describeCauldron(Blocks.LAVA_CAULDRON, LavaDescriptor.INSTANCE);

        registrar.addBlockData(FurnaceProvider.INSTANCE, AbstractFurnaceBlockEntity.class);
        registrar.addBlockData(EnderChestProvider.INSTANCE, EnderChestBlockEntity.class);

        registrar.addBlockData(RandomizableContainerProvider.INSTANCE, RandomizableContainerBlockEntity.class, 1100);
        registrar.addBlockData(BaseContainerProvider.INSTANCE, BaseContainerBlockEntity.class, 1200);
        registrar.addBlockData(HopperContainerProvider.INSTANCE, BlockEntity.class, 1300);

        registrar.addEntityData(ContainerEntityProvider.INSTANCE, Container.class, 1100);

        defaultRollbackBlacklistConfig = IJsonConfig.of(DefaultRollbackBlacklistConfig.class)
                .file(WailaConstants.WAILA + "/default_blacklist")
                .gson(new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(DefaultRollbackBlacklistConfig.class, new DefaultRollbackBlacklistConfig.Adapter())
                    .create())
                .build();
        if (!defaultRollbackBlacklistConfig.isFileExists()) {
            defaultRollbackBlacklistConfig.get().blocks.add(Blocks.BARRIER);
            defaultRollbackBlacklistConfig.get().blocks.add(Blocks.STRUCTURE_VOID);
            defaultRollbackBlacklistConfig.get().entityTypes.add(EntityType.AREA_EFFECT_CLOUD);
            defaultRollbackBlacklistConfig.get().entityTypes.add(EntityType.SNOWBALL);
            defaultRollbackBlacklistConfig.get().entityTypes.add(EntityType.EXPERIENCE_ORB);
            defaultRollbackBlacklistConfig.get().entityTypes.add(EntityType.INTERACTION);
            defaultRollbackBlacklistConfig.get().entityTypes.add(EntityType.FIREBALL);
            defaultRollbackBlacklistConfig.get().entityTypes.add(EntityType.FIREWORK_ROCKET);
        }
        defaultRollbackBlacklistConfig.save();
        registrar.addConfig(new ResourceLocation(WailaConstants.WAILA, "default_blacklist"), defaultRollbackBlacklistConfig.getPath());

        for (Block b : defaultRollbackBlacklistConfig.get().blocks) {
            registrar.addBlacklist(b);
        }

        for (BlockEntityType<?> e : defaultRollbackBlacklistConfig.get().blockEntityTypes) {
            registrar.addBlacklist(e);
        }

        for (EntityType<?> e : defaultRollbackBlacklistConfig.get().entityTypes) {
            registrar.addBlacklist(e);
        }
    }

}
