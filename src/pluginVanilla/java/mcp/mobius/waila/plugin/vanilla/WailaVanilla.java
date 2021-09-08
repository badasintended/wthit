package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.plugin.vanilla.component.BeehiveComponent;
import mcp.mobius.waila.plugin.vanilla.component.ComposterComponent;
import mcp.mobius.waila.plugin.vanilla.component.EntityIconComponent;
import mcp.mobius.waila.plugin.vanilla.component.FallingBlockComponent;
import mcp.mobius.waila.plugin.vanilla.component.FurnaceComponent;
import mcp.mobius.waila.plugin.vanilla.component.InfestedBlockComponent;
import mcp.mobius.waila.plugin.vanilla.component.ItemEntityComponent;
import mcp.mobius.waila.plugin.vanilla.component.JukeboxComponent;
import mcp.mobius.waila.plugin.vanilla.component.NoteBlockComponent;
import mcp.mobius.waila.plugin.vanilla.component.PetOwnerComponent;
import mcp.mobius.waila.plugin.vanilla.component.PlantComponent;
import mcp.mobius.waila.plugin.vanilla.component.PlayerHeadComponent;
import mcp.mobius.waila.plugin.vanilla.component.PowderSnowComponent;
import mcp.mobius.waila.plugin.vanilla.component.RedstoneComponent;
import mcp.mobius.waila.plugin.vanilla.component.SpawnerComponent;
import mcp.mobius.waila.plugin.vanilla.component.TrappedChestComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.config.Options.NoteDisplayMode;
import mcp.mobius.waila.plugin.vanilla.renderer.ItemRenderer;
import mcp.mobius.waila.plugin.vanilla.renderer.ProgressRenderer;
import mcp.mobius.waila.plugin.vanilla.renderer.Renderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.BeehiveBlock;
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
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class WailaVanilla implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.addSyncedConfig(Options.OVERRIDE_INFESTED, true);
        registrar.addSyncedConfig(Options.OVERRIDE_TRAPPED_CHEST, true);
        registrar.addSyncedConfig(Options.OVERRIDE_POWDER_SNOW, true);
        registrar.addSyncedConfig(Options.PET_OWNER, true);

        registrar.addConfig(Options.FURNACE_CONTENTS, true);
        registrar.addConfig(Options.SPAWNER_TYPE, true);
        registrar.addConfig(Options.CROP_PROGRESS, true);
        registrar.addConfig(Options.REDSTONE_LEVER, true);
        registrar.addConfig(Options.REDSTONE_REPEATER, true);
        registrar.addConfig(Options.REDSTONE_COMPARATOR, true);
        registrar.addConfig(Options.REDSTONE_LEVEL, true);
        registrar.addConfig(Options.JUKEBOX_RECORD, true);
        registrar.addConfig(Options.PLAYER_HEAD_NAME, true);
        registrar.addConfig(Options.LEVEL_COMPOSTER, true);
        registrar.addConfig(Options.LEVEL_HONEY, true);
        registrar.addConfig(Options.NOTE_BLOCK_TYPE, true);
        registrar.addConfig(Options.NOTE_BLOCK_NOTE, NoteDisplayMode.SHARP);
        registrar.addConfig(Options.NOTE_BLOCK_INT_VALUE, false);
        registrar.addConfig(Options.PET_HIDE_UNKNOWN_OWNER, false);

        registrar.addRenderer(Renderers.ITEM, new ItemRenderer());
        registrar.addRenderer(Renderers.PROGRESS, new ProgressRenderer());

        registrar.addOverride(InfestedBlockComponent.INSTANCE, InfestedBlock.class);
        registrar.addOverride(TrappedChestComponent.INSTANCE, TrappedChestBlock.class);
        registrar.addOverride(PowderSnowComponent.INSTANCE, PowderSnowBlock.class);

        registrar.addDisplayItem(PlayerHeadComponent.INSTANCE, SkullBlockEntity.class);
        registrar.addComponent(PlayerHeadComponent.INSTANCE, BODY, SkullBlockEntity.class);

        registrar.addComponent(SpawnerComponent.INSTANCE, HEAD, SpawnerBlockEntity.class, 950);

        registrar.addDisplayItem(PlantComponent.INSTANCE, CropBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, CropBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, StemBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, CocoaBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, NetherWartBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, SweetBerryBushBlock.class);

        registrar.addComponent(RedstoneComponent.INSTANCE, BODY, LeverBlock.class);
        registrar.addComponent(RedstoneComponent.INSTANCE, BODY, RepeaterBlock.class);
        registrar.addComponent(RedstoneComponent.INSTANCE, BODY, ComparatorBlock.class);
        registrar.addComponent(RedstoneComponent.INSTANCE, BODY, RedStoneWireBlock.class);

        registrar.addComponent(JukeboxComponent.INSTANCE, BODY, JukeboxBlockEntity.class);
        registrar.addBlockData(JukeboxComponent.INSTANCE, JukeboxBlockEntity.class);

        registrar.addComponent(FallingBlockComponent.INSTANCE, HEAD, FallingBlockEntity.class);
        registrar.addDisplayItem(FallingBlockComponent.INSTANCE, FallingBlockEntity.class);

        registrar.addDisplayItem(EntityIconComponent.INSTANCE, Entity.class);

        registrar.addDisplayItem(ItemEntityComponent.INSTANCE, ItemEntity.class);
        registrar.addComponent(ItemEntityComponent.INSTANCE, HEAD, ItemEntity.class, 950);
        registrar.addComponent(ItemEntityComponent.INSTANCE, TAIL, ItemEntity.class, 950);

        registrar.addComponent(FurnaceComponent.INSTANCE, BODY, AbstractFurnaceBlockEntity.class);
        registrar.addBlockData(FurnaceComponent.INSTANCE, AbstractFurnaceBlockEntity.class);

        registrar.addComponent(ComposterComponent.INSTANCE, BODY, ComposterBlock.class);

        registrar.addComponent(BeehiveComponent.INSTANCE, BODY, BeehiveBlock.class);

        registrar.addComponent(NoteBlockComponent.INSTANCE, BODY, NoteBlock.class);

        registrar.addComponent(PetOwnerComponent.INSTANCE, BODY, Entity.class);
    }

}
