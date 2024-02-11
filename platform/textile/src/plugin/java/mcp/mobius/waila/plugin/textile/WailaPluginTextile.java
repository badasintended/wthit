package mcp.mobius.waila.plugin.textile;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IToolType;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.plugin.textile.fluid.TextileFluidDescriptor;
import mcp.mobius.waila.plugin.textile.provider.FluidStorageProvider;
import mcp.mobius.waila.plugin.textile.provider.ItemStorageProvider;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;

public abstract class WailaPluginTextile implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        FluidData.describeFluid(Fluid.class, TextileFluidDescriptor.INSTANCE);
        FluidData.describeCauldron(Block.class, TextileFluidDescriptor.INSTANCE);
        registrar.addBlockData(ItemStorageProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.addBlockData(FluidStorageProvider.INSTANCE, BlockEntity.class, 2000);

        registrar.addToolType(new ResourceLocation("pickaxe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_PICKAXE)
            .blockTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .itemTag(ItemTags.PICKAXES)
            .build());

        registrar.addToolType(new ResourceLocation("shovel"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_SHOVEL)
            .blockTag(BlockTags.MINEABLE_WITH_SHOVEL)
            .itemTag(ItemTags.SHOVELS)
            .build());

        registrar.addToolType(new ResourceLocation("axe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_AXE)
            .blockTag(BlockTags.MINEABLE_WITH_AXE)
            .itemTag(ItemTags.AXES)
            .build());

        registrar.addToolType(new ResourceLocation("hoe"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_HOE)
            .blockTag(BlockTags.MINEABLE_WITH_HOE)
            .itemTag(ItemTags.HOES)
            .build());

        registrar.addToolType(new ResourceLocation("sword"), IToolType.builder()
            .lowestTierItem(Items.WOODEN_SWORD)
            .blockTag(FabricMineableTags.SWORD_MINEABLE)
            .itemTag(ItemTags.SWORDS)
            .build());

        // TODO: not actually working for blocks that instamine, like short grass
        //       maybe look to currently used plugin (cimtb) implementation
        var shearsStack = new ItemStack(Items.SHEARS);
        registrar.addToolType(new ResourceLocation("shears"), IToolType.builder()
            .lowestTierStack(shearsStack)
            .blockPredicate(it -> it.is(FabricMineableTags.SHEARS_MINEABLE) || shearsStack.getDestroySpeed(it) > 1.0F)
            .itemTag(ConventionalItemTags.SHEARS)
            .build());
    }

}
