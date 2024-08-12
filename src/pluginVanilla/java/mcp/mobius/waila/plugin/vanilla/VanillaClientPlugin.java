package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IWailaClientPlugin;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.plugin.vanilla.fluid.LavaDescriptor;
import mcp.mobius.waila.plugin.vanilla.fluid.WaterDescriptor;
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
import mcp.mobius.waila.plugin.vanilla.provider.ItemFrameProvider;
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
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.material.Fluids;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class VanillaClientPlugin implements IWailaClientPlugin {

    @Override
    public void register(IClientRegistrar registrar) {
        registrar.addComponent(BlockAttributesProvider.INSTANCE, BODY, Block.class, 950);

        registrar.addIcon(ItemEntityProvider.INSTANCE, ItemEntity.class);
        registrar.addComponent(ItemEntityProvider.INSTANCE, HEAD, ItemEntity.class, 950);
        registrar.addComponent(ItemEntityProvider.INSTANCE, BODY, ItemEntity.class, 950);
        registrar.addComponent(ItemEntityProvider.INSTANCE, TAIL, ItemEntity.class, 950);
        registrar.addOverride(ItemEntityProvider.INSTANCE, ItemEntity.class);

        registrar.addComponent(EntityAttributesProvider.INSTANCE, HEAD, Entity.class, 950);
        registrar.addComponent(EntityAttributesProvider.INSTANCE, BODY, Entity.class, 950);

        registrar.addComponent(PetOwnerProvider.INSTANCE, BODY, OwnableEntity.class);

        registrar.addComponent(HorseProvider.INSTANCE, BODY, AbstractHorse.class);

        registrar.addComponent(PandaProvider.INSTANCE, BODY, Panda.class);

        registrar.addComponent(BeehiveProvider.INSTANCE, BODY, BeehiveBlock.class);
        registrar.addComponent(BeeProvider.INSTANCE, BODY, Bee.class);

        registrar.addComponent(BeaconProvider.INSTANCE, BODY, BeaconBlockEntity.class);

        registrar.addComponent(MobEffectProvider.INSTANCE, BODY, LivingEntity.class);

        registrar.addComponent(JukeboxProvider.INSTANCE, BODY, JukeboxBlockEntity.class);

        registrar.addComponent(MobTimerProvider.INSTANCE, BODY, AgeableMob.class);

        registrar.addOverride(InvisibleEntityProvider.INSTANCE, LivingEntity.class);
        registrar.addOverride(InfestedBlockProvider.INSTANCE, InfestedBlock.class);
        registrar.addOverride(TrappedChestProvider.INSTANCE, TrappedChestBlock.class);
        registrar.addOverride(PowderSnowProvider.INSTANCE, PowderSnowBlock.class);
        registrar.addOverride(VehicleProvider.INSTANCE, Entity.class, 900);

        registrar.addEventListener(BreakProgressProvider.INSTANCE);

        registrar.addComponent(SpawnerProvider.INSTANCE, HEAD, SpawnerBlockEntity.class, 950);

        registrar.addIcon(PlantProvider.INSTANCE, CropBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, CropBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, StemBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, CocoaBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, NetherWartBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, SweetBerryBushBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, SaplingBlock.class);

        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, LeverBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, RepeaterBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, ComparatorBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, RedStoneWireBlock.class);

        registrar.addIcon(PlayerHeadProvider.INSTANCE, SkullBlockEntity.class);
        registrar.addComponent(PlayerHeadProvider.INSTANCE, BODY, SkullBlockEntity.class);

        registrar.addComponent(ComposterProvider.INSTANCE, BODY, ComposterBlock.class);

        registrar.addComponent(NoteBlockProvider.INSTANCE, BODY, NoteBlock.class);

        registrar.addIcon(FallingBlockProvider.INSTANCE, FallingBlockEntity.class);
        registrar.addComponent(FallingBlockProvider.INSTANCE, HEAD, FallingBlockEntity.class);

        registrar.addComponent(BoatProvider.INSTANCE, HEAD, Boat.class, 950);
        registrar.addComponent(BoatProvider.INSTANCE, TAIL, Boat.class, 950);

        registrar.addIcon(ItemFrameProvider.INSTANCE, ItemFrame.class);
        registrar.addComponent(ItemFrameProvider.INSTANCE, HEAD, ItemFrame.class);
        registrar.addComponent(ItemFrameProvider.INSTANCE, BODY, ItemFrame.class);
        registrar.addComponent(ItemFrameProvider.INSTANCE, TAIL, ItemFrame.class);

        registrar.addIcon(ChiseledBookShelfProvider.INSTANCE, ChiseledBookShelfBlock.class);
        registrar.addComponent(ChiseledBookShelfProvider.INSTANCE, HEAD, ChiseledBookShelfBlock.class);
        registrar.addComponent(ChiseledBookShelfProvider.INSTANCE, BODY, ChiseledBookShelfBlock.class);
        registrar.addComponent(ChiseledBookShelfProvider.INSTANCE, TAIL, ChiseledBookShelfBlock.class);

        FluidData.describeFluid(Fluids.WATER, WaterDescriptor.INSTANCE);
        FluidData.describeFluid(Fluids.LAVA, LavaDescriptor.INSTANCE);
        FluidData.describeCauldron(Blocks.WATER_CAULDRON, WaterDescriptor.INSTANCE);
        FluidData.describeCauldron(Blocks.LAVA_CAULDRON, LavaDescriptor.INSTANCE);
    }

}
