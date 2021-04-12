package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

public enum PlayerHeadComponent implements IBlockComponentProvider {

    INSTANCE;

    static final ItemStack PLAYER_HEAD_STACK = new ItemStack(Items.PLAYER_HEAD);

    @Override
    public ItemStack getDisplayItem(IBlockAccessor accessor, IPluginConfig config) {
        SkullBlockEntity skull = (SkullBlockEntity) accessor.getBlockEntity();
        if (skull.getOwner() != null) {
            NbtCompound tag = PLAYER_HEAD_STACK.getOrCreateTag();
            NbtCompound skullOwner = tag.getCompound("SkullOwner");
            tag.put("SkullOwner", NbtHelper.writeGameProfile(skullOwner, skull.getOwner()));
            return PLAYER_HEAD_STACK;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.get(WailaVanilla.CONFIG_PLAYER_HEAD_NAME)) {
            SkullBlockEntity skull = (SkullBlockEntity) accessor.getBlockEntity();
            if (skull.getOwner() != null && !StringUtils.isBlank(skull.getOwner().getName()))
                tooltip.add(new LiteralText(skull.getOwner().getName()));
        }
    }

}
