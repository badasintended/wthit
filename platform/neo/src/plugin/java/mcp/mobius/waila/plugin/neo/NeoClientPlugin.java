package mcp.mobius.waila.plugin.neo;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IToolType;
import mcp.mobius.waila.api.IWailaClientPlugin;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.plugin.neo.fluid.NeoFluidDescriptor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.ItemAbilities;

public class NeoClientPlugin implements IWailaClientPlugin {

    @Override
    public void register(IClientRegistrar registrar) {
        FluidData.describeFluid(Fluid.class, NeoFluidDescriptor.INSTANCE);

        registrar.toolType(ResourceLocation.withDefaultNamespace("pickaxe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_PICKAXE)
            .blockTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .itemPredicate(it -> it.canPerformAction(ItemAbilities.PICKAXE_DIG))
            .build());

        registrar.toolType(ResourceLocation.withDefaultNamespace("shovel"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_SHOVEL)
            .blockTag(BlockTags.MINEABLE_WITH_SHOVEL)
            .itemPredicate(it -> it.canPerformAction(ItemAbilities.SHOVEL_DIG))
            .build());

        registrar.toolType(ResourceLocation.withDefaultNamespace("axe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_AXE)
            .blockTag(BlockTags.MINEABLE_WITH_AXE)
            .itemPredicate(it -> it.canPerformAction(ItemAbilities.AXE_DIG))
            .build());

        registrar.toolType(ResourceLocation.withDefaultNamespace("hoe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_HOE)
            .blockTag(BlockTags.MINEABLE_WITH_HOE)
            .itemPredicate(it -> it.canPerformAction(ItemAbilities.HOE_DIG))
            .build());

        registrar.toolType(ResourceLocation.withDefaultNamespace("sword"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_SWORD)
            .blockTag(BlockTags.SWORD_EFFICIENT)
            .itemPredicate(it -> it.canPerformAction(ItemAbilities.SWORD_DIG))
            .build());

        registrar.toolType(ResourceLocation.withDefaultNamespace("shears"), IToolType.builder()
            .lowestTierItem(Items.SHEARS)
            .blockPredicate(it -> it.getBlock() instanceof IShearable || it.getBlock() instanceof DoublePlantBlock)
            .itemPredicate(it -> it.canPerformAction(ItemAbilities.SHEARS_DIG))
            .build());
    }

}
