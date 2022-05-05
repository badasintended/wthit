package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public enum PlayerHeadProvider implements IBlockComponentProvider {

    INSTANCE;

    static final ItemComponent PLAYER_HEAD_STACK = new ItemComponent(Items.PLAYER_HEAD);

    @Nullable
    @Override
    public ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        SkullBlockEntity skull = accessor.getBlockEntity();
        if (skull != null && skull.getOwnerProfile() != null) {
            CompoundTag tag = PLAYER_HEAD_STACK.stack.getOrCreateTag();
            CompoundTag skullOwner = tag.getCompound("SkullOwner");
            tag.put("SkullOwner", NbtUtils.writeGameProfile(skullOwner, skull.getOwnerProfile()));
            return PLAYER_HEAD_STACK;
        }
        return null;
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.PLAYER_HEAD_NAME)) {
            SkullBlockEntity skull = accessor.getBlockEntity();
            if (skull != null && skull.getOwnerProfile() != null && !StringUtils.isBlank(skull.getOwnerProfile().getName())) {
                tooltip.addLine(Component.translatable(skull.getOwnerProfile().getName()));
            }
        }
    }

}
