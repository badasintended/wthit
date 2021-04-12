package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public enum SpawnerComponent implements IBlockComponentProvider {

    INSTANCE;

    static final Identifier OBJECT_NAME_TAG = Waila.id("object_name");

    @Override
    public void appendHead(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.get(WailaVanilla.CONFIG_SPAWNER_TYPE)) {
            MobSpawnerBlockEntity spawner = (MobSpawnerBlockEntity) accessor.getBlockEntity();
            ((ITaggableList<Identifier, Text>) tooltip).setTag(OBJECT_NAME_TAG, new TranslatableText(accessor.getBlock().getTranslationKey())
                .append(new LiteralText(" ("))
                .append(spawner.getLogic().getRenderedEntity(accessor.getWorld()).getDisplayName())
                .append(new LiteralText(")"))
            );
        }
    }

}
