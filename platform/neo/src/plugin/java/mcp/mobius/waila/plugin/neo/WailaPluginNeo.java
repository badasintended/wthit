package mcp.mobius.waila.plugin.neo;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IToolType;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.plugin.neo.fluid.NeoFluidDescriptor;
import mcp.mobius.waila.plugin.neo.provider.EnergyCapabilityProvider;
import mcp.mobius.waila.plugin.neo.provider.FluidCapabilityProvider;
import mcp.mobius.waila.plugin.neo.provider.ItemCapabilityProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.ItemAbilities;

public class WailaPluginNeo implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        FluidData.describeFluid(Fluid.class, NeoFluidDescriptor.INSTANCE);
        registrar.addBlockData(EnergyCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.addBlockData(ItemCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.addBlockData(FluidCapabilityProvider.INSTANCE, BlockEntity.class, 2000);

        registrar.addToolType(ResourceLocation.withDefaultNamespace("pickaxe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_PICKAXE)
            .blockTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .itemPredicate(it -> it.canPerformAction(ItemAbilities.PICKAXE_DIG))
            .build());

        registrar.addToolType(ResourceLocation.withDefaultNamespace("shovel"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_SHOVEL)
            .blockTag(BlockTags.MINEABLE_WITH_SHOVEL)
            .itemPredicate(it -> it.canPerformAction(ItemAbilities.SHOVEL_DIG))
            .build());

        registrar.addToolType(ResourceLocation.withDefaultNamespace("axe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_AXE)
            .blockTag(BlockTags.MINEABLE_WITH_AXE)
            .itemPredicate(it -> it.canPerformAction(ItemAbilities.AXE_DIG))
            .build());

        registrar.addToolType(ResourceLocation.withDefaultNamespace("hoe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_HOE)
            .blockTag(BlockTags.MINEABLE_WITH_HOE)
            .itemPredicate(it -> it.canPerformAction(ItemAbilities.HOE_DIG))
            .build());

        registrar.addToolType(ResourceLocation.withDefaultNamespace("sword"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_SWORD)
            .blockTag(BlockTags.SWORD_EFFICIENT)
            .itemPredicate(it -> it.canPerformAction(ItemAbilities.SWORD_DIG))
            .build());

        registrar.addToolType(ResourceLocation.withDefaultNamespace("shears"), IToolType.builder()
            .lowestTierItem(Items.SHEARS)
            .blockPredicate(it -> it.getBlock() instanceof IShearable || it.getBlock() instanceof DoublePlantBlock)
            .itemPredicate(it -> it.canPerformAction(ItemAbilities.SHEARS_DIG))
            .build());
    }

}
