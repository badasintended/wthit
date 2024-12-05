package mcp.mobius.waila.plugin.textile;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IToolType;
import mcp.mobius.waila.api.IWailaClientPlugin;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.mixed.IShearable;
import mcp.mobius.waila.plugin.textile.fluid.TextileFluidDescriptor;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.material.Fluid;

public abstract class TextileClientPlugin implements IWailaClientPlugin {

    @Override
    public void register(IClientRegistrar registrar) {
        FluidData.describeFluid(Fluid.class, TextileFluidDescriptor.INSTANCE);
        FluidData.describeCauldron(Block.class, TextileFluidDescriptor.INSTANCE);

        registrar.toolType(ResourceLocation.withDefaultNamespace("pickaxe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_PICKAXE)
            .blockTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .itemTag(ItemTags.PICKAXES)
            .build());

        registrar.toolType(ResourceLocation.withDefaultNamespace("shovel"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_SHOVEL)
            .blockTag(BlockTags.MINEABLE_WITH_SHOVEL)
            .itemTag(ItemTags.SHOVELS)
            .build());

        registrar.toolType(ResourceLocation.withDefaultNamespace("axe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_AXE)
            .blockTag(BlockTags.MINEABLE_WITH_AXE)
            .itemTag(ItemTags.AXES)
            .build());

        registrar.toolType(ResourceLocation.withDefaultNamespace("hoe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_HOE)
            .blockTag(BlockTags.MINEABLE_WITH_HOE)
            .itemTag(ItemTags.HOES)
            .build());

        registrar.toolType(ResourceLocation.withDefaultNamespace("sword"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_SWORD)
            .blockTag(BlockTags.SWORD_EFFICIENT)
            .itemTag(ItemTags.SWORDS)
            .build());

        // TODO
        registrar.toolType(ResourceLocation.withDefaultNamespace("shears"), IToolType.builder()
            .lowestTierItem(Items.SHEARS)
            .blockPredicate(it -> /*it.is(BlockTags.SHEARS_MINEABLE) ||*/ it.getBlock() instanceof IShearable || it.getBlock() instanceof DoublePlantBlock)
            .itemTag(ConventionalItemTags.SHEAR_TOOLS)
            .build());
    }

}
