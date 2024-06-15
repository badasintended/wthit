package mcp.mobius.waila.plugin.forge;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IToolType;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.plugin.forge.fluid.ForgeFluidDescriptor;
import mcp.mobius.waila.plugin.forge.provider.EnergyCapabilityProvider;
import mcp.mobius.waila.plugin.forge.provider.FluidCapabilityProvider;
import mcp.mobius.waila.plugin.forge.provider.ItemCapabilityProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolActions;

public class WailaPluginForge implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        FluidData.describeFluid(Fluid.class, ForgeFluidDescriptor.INSTANCE);
        registrar.addBlockData(EnergyCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.addBlockData(ItemCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.addBlockData(FluidCapabilityProvider.INSTANCE, BlockEntity.class, 2000);

        registrar.addToolType(ResourceLocation.withDefaultNamespace("pickaxe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_PICKAXE)
            .blockTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .itemPredicate(it -> it.canPerformAction(ToolActions.PICKAXE_DIG))
            .build());

        registrar.addToolType(ResourceLocation.withDefaultNamespace("shovel"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_SHOVEL)
            .blockTag(BlockTags.MINEABLE_WITH_SHOVEL)
            .itemPredicate(it -> it.canPerformAction(ToolActions.SHOVEL_DIG))
            .build());

        registrar.addToolType(ResourceLocation.withDefaultNamespace("axe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_AXE)
            .blockTag(BlockTags.MINEABLE_WITH_AXE)
            .itemPredicate(it -> it.canPerformAction(ToolActions.AXE_DIG))
            .build());

        registrar.addToolType(ResourceLocation.withDefaultNamespace("hoe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_HOE)
            .blockTag(BlockTags.MINEABLE_WITH_HOE)
            .itemPredicate(it -> it.canPerformAction(ToolActions.HOE_DIG))
            .build());

        registrar.addToolType(ResourceLocation.withDefaultNamespace("sword"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_SWORD)
            .blockTag(ResourceLocation.parse("forge:mineable/sword"))
            .itemPredicate(it -> it.canPerformAction(ToolActions.SWORD_DIG))
            .build());

        registrar.addToolType(ResourceLocation.withDefaultNamespace("shears"), IToolType.builder()
            .lowestTierItem(Items.SHEARS)
            .blockPredicate(it -> it.getBlock() instanceof IForgeShearable || it.getBlock() instanceof DoublePlantBlock)
            .itemPredicate(it -> it.canPerformAction(ToolActions.SHEARS_DIG))
            .build());
    }

}
