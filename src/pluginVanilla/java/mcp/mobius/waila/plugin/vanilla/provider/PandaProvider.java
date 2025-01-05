package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Panda;

public enum PandaProvider implements IEntityComponentProvider {

    INSTANCE;

    private static final ResourceLocation PERSONALITY = Options.PANDA_GENES.withSuffix(".personality");
    private static final ResourceLocation TRAITS = Options.PANDA_GENES.withSuffix(".traits");

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.PANDA_GENES)) return;

        Panda panda = accessor.getEntity();
        var personality = panda.getVariant();
        var mainGene = panda.getMainGene();
        var hiddenGene = panda.getHiddenGene();

        tooltip.setLine(PERSONALITY, new PairComponent(
            Component.translatable(Tl.Tooltip.Panda.PERSONALITY),
            geneText(personality)));
        tooltip.setLine(TRAITS, new PairComponent(
            Component.translatable(Tl.Tooltip.Panda.TRAITS),
            Component.empty().append(geneText(mainGene)).append(", ").append(geneText(hiddenGene))));
    }

    private static MutableComponent geneText(Panda.Gene gene) {
        var text = Component.translatable(Tl.Tooltip.Panda.GENE + "." + gene.getSerializedName());
        if (gene.isRecessive()) text.withStyle(ChatFormatting.RED);
        return text;
    }

}
