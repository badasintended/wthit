package mcp.mobius.waila.plugin.forge;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IToolType;
import mcp.mobius.waila.api.IWailaClientPlugin;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.mixed.IShearable;
import mcp.mobius.waila.plugin.forge.fluid.ForgeFluidDescriptor;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ToolActions;

public class ForgeClientPlugin implements IWailaClientPlugin {

    @Override
    public void register(IClientRegistrar registrar) {
        // TODO(26.1)
        // FluidData.describeFluid(Fluid.class, ForgeFluidDescriptor.INSTANCE);

        registrar.toolType(Identifier.withDefaultNamespace("pickaxe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_PICKAXE)
            .blockTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .itemPredicate(it -> it.canPerformAction(ToolActions.PICKAXE_DIG))
            .build());

        registrar.toolType(Identifier.withDefaultNamespace("shovel"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_SHOVEL)
            .blockTag(BlockTags.MINEABLE_WITH_SHOVEL)
            .itemPredicate(it -> it.canPerformAction(ToolActions.SHOVEL_DIG))
            .build());

        registrar.toolType(Identifier.withDefaultNamespace("axe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_AXE)
            .blockTag(BlockTags.MINEABLE_WITH_AXE)
            .itemPredicate(it -> it.canPerformAction(ToolActions.AXE_DIG))
            .build());

        registrar.toolType(Identifier.withDefaultNamespace("hoe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_HOE)
            .blockTag(BlockTags.MINEABLE_WITH_HOE)
            .itemPredicate(it -> it.canPerformAction(ToolActions.HOE_DIG))
            .build());

        registrar.toolType(Identifier.withDefaultNamespace("sword"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_SWORD)
            .blockTag(Identifier.parse("forge:mineable/sword"))
            .itemPredicate(it -> it.canPerformAction(ToolActions.SWORD_DIG))
            .build());

        registrar.toolType(Identifier.withDefaultNamespace("shears"), IToolType.builder()
            .lowestTierItem(Items.SHEARS)
            .blockPredicate(it -> it.getBlock() instanceof IShearable || it.getBlock() instanceof DoublePlantBlock)
            .itemPredicate(it -> it.canPerformAction(ToolActions.SHEARS_DIG))
            .build());
    }

}
