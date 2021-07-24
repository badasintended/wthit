package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.StringUtils;

public enum PlayerHeadComponent implements IBlockComponentProvider {

    INSTANCE;

    static final ItemStack PLAYER_HEAD_STACK = new ItemStack(Items.PLAYER_HEAD);

    @Override
    public ItemStack getDisplayItem(IBlockAccessor accessor, IPluginConfig config) {
        SkullBlockEntity skull = (SkullBlockEntity) accessor.getBlockEntity();
        if (skull != null && skull.getOwnerProfile() != null) {
            CompoundTag tag = PLAYER_HEAD_STACK.getOrCreateTag();
            CompoundTag skullOwner = tag.getCompound("SkullOwner");
            tag.put("SkullOwner", NbtUtils.writeGameProfile(skullOwner, skull.getOwnerProfile()));
            return PLAYER_HEAD_STACK;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void appendBody(List<Component> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.get(WailaVanilla.CONFIG_PLAYER_HEAD_NAME)) {
            SkullBlockEntity skull = (SkullBlockEntity) accessor.getBlockEntity();
            if (skull != null && skull.getOwnerProfile() != null && !StringUtils.isBlank(skull.getOwnerProfile().getName()))
                tooltip.add(new TextComponent(skull.getOwnerProfile().getName()));
        }
    }

}
