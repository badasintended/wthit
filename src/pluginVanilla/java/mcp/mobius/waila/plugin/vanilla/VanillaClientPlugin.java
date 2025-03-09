package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IWailaClientPlugin;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.plugin.vanilla.fluid.LavaDescriptor;
import mcp.mobius.waila.plugin.vanilla.fluid.WaterDescriptor;
import mcp.mobius.waila.plugin.vanilla.provider.BannerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BeaconProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BeeProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BeehiveProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BlockAttributesProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BoatProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BreakProgressProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ChiseledBookShelfProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ComposterProvider;
import mcp.mobius.waila.plugin.vanilla.provider.EntityAttributesProvider;
import mcp.mobius.waila.plugin.vanilla.provider.FallingBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.HorseProvider;
import mcp.mobius.waila.plugin.vanilla.provider.InfestedBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.InvisibleEntityProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ItemEntityProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ItemHolderProvider;
import mcp.mobius.waila.plugin.vanilla.provider.JukeboxProvider;
import mcp.mobius.waila.plugin.vanilla.provider.MobEffectProvider;
import mcp.mobius.waila.plugin.vanilla.provider.MobTimerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.NoteBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PandaProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PetOwnerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PlantProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PlayerHeadProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PowderSnowProvider;
import mcp.mobius.waila.plugin.vanilla.provider.RedstoneProvider;
import mcp.mobius.waila.plugin.vanilla.provider.SpawnerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.TrappedChestProvider;
import mcp.mobius.waila.plugin.vanilla.provider.VehicleProvider;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
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
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.material.Fluids;

public class VanillaClientPlugin implements IWailaClientPlugin {

    @Override
    public void register(IClientRegistrar registrar) {
        registrar.body(BlockAttributesProvider.INSTANCE, Block.class, 950);

        registrar.head(BannerProvider.INSTANCE, BannerBlockEntity.class, 950);

        registrar.icon(ItemEntityProvider.INSTANCE, ItemEntity.class);
        registrar.head(ItemEntityProvider.INSTANCE, ItemEntity.class, 950);
        registrar.body(ItemEntityProvider.INSTANCE, ItemEntity.class, 950);
        registrar.tail(ItemEntityProvider.INSTANCE, ItemEntity.class, 950);
        registrar.override(ItemEntityProvider.INSTANCE, ItemEntity.class);

        registrar.head(EntityAttributesProvider.INSTANCE, Entity.class, 950);
        registrar.body(EntityAttributesProvider.INSTANCE, Entity.class, 950);

        registrar.body(PetOwnerProvider.INSTANCE, OwnableEntity.class);

        registrar.body(HorseProvider.INSTANCE, AbstractHorse.class);

        registrar.body(PandaProvider.INSTANCE, Panda.class);

        registrar.body(BeehiveProvider.INSTANCE, BeehiveBlock.class);
        registrar.body(BeeProvider.INSTANCE, Bee.class);

        registrar.body(BeaconProvider.INSTANCE, BeaconBlockEntity.class);

        registrar.body(MobEffectProvider.INSTANCE, LivingEntity.class);

        registrar.body(JukeboxProvider.INSTANCE, JukeboxBlockEntity.class);

        registrar.body(MobTimerProvider.INSTANCE, AgeableMob.class);

        registrar.override(InvisibleEntityProvider.INSTANCE, LivingEntity.class);
        registrar.override(InfestedBlockProvider.INSTANCE, InfestedBlock.class);
        registrar.override(TrappedChestProvider.INSTANCE, TrappedChestBlock.class);
        registrar.override(PowderSnowProvider.INSTANCE, PowderSnowBlock.class);
        registrar.override(VehicleProvider.INSTANCE, Entity.class, 900);

        registrar.eventListener(BreakProgressProvider.INSTANCE);

        registrar.head(SpawnerProvider.INSTANCE, SpawnerBlockEntity.class, 950);

        registrar.body(PlantProvider.INSTANCE, CropBlock.class);
        registrar.body(PlantProvider.INSTANCE, StemBlock.class);
        registrar.body(PlantProvider.INSTANCE, CocoaBlock.class);
        registrar.body(PlantProvider.INSTANCE, NetherWartBlock.class);
        registrar.body(PlantProvider.INSTANCE, SweetBerryBushBlock.class);
        registrar.body(PlantProvider.INSTANCE, SaplingBlock.class);

        registrar.icon(PlantProvider.INSTANCE, CropBlock.class);

        registrar.body(RedstoneProvider.INSTANCE, LeverBlock.class);
        registrar.body(RedstoneProvider.INSTANCE, RepeaterBlock.class);
        registrar.body(RedstoneProvider.INSTANCE, ComparatorBlock.class);
        registrar.body(RedstoneProvider.INSTANCE, RedStoneWireBlock.class);

        registrar.icon(PlayerHeadProvider.INSTANCE, SkullBlockEntity.class);
        registrar.body(PlayerHeadProvider.INSTANCE, SkullBlockEntity.class);

        registrar.body(ComposterProvider.INSTANCE, ComposterBlock.class);

        registrar.body(NoteBlockProvider.INSTANCE, NoteBlock.class);

        registrar.icon(FallingBlockProvider.INSTANCE, FallingBlockEntity.class);
        registrar.head(FallingBlockProvider.INSTANCE, FallingBlockEntity.class);

        registrar.head(BoatProvider.INSTANCE, Boat.class, 950);
        registrar.tail(BoatProvider.INSTANCE, Boat.class, 950);

        registrar.icon(ItemHolderProvider.ITEM_FRAME, ItemFrame.class);
        registrar.head(ItemHolderProvider.ITEM_FRAME, ItemFrame.class);
        registrar.body(ItemHolderProvider.ITEM_FRAME, ItemFrame.class);
        registrar.tail(ItemHolderProvider.ITEM_FRAME, ItemFrame.class);

        registrar.icon(ItemHolderProvider.OMINOUS_ITEM_SPAWNER, OminousItemSpawner.class);
        registrar.head(ItemHolderProvider.OMINOUS_ITEM_SPAWNER, OminousItemSpawner.class);
        registrar.body(ItemHolderProvider.OMINOUS_ITEM_SPAWNER, OminousItemSpawner.class);
        registrar.tail(ItemHolderProvider.OMINOUS_ITEM_SPAWNER, OminousItemSpawner.class);

        registrar.icon(ChiseledBookShelfProvider.INSTANCE, ChiseledBookShelfBlock.class);
        registrar.head(ChiseledBookShelfProvider.INSTANCE, ChiseledBookShelfBlock.class);
        registrar.body(ChiseledBookShelfProvider.INSTANCE, ChiseledBookShelfBlock.class);
        registrar.tail(ChiseledBookShelfProvider.INSTANCE, ChiseledBookShelfBlock.class);

        FluidData.describeFluid(Fluids.WATER, WaterDescriptor.INSTANCE);
        FluidData.describeFluid(Fluids.LAVA, LavaDescriptor.INSTANCE);

        FluidData.describeCauldron(Blocks.WATER_CAULDRON, WaterDescriptor.INSTANCE);
        FluidData.describeCauldron(Blocks.LAVA_CAULDRON, LavaDescriptor.INSTANCE);
    }

}
