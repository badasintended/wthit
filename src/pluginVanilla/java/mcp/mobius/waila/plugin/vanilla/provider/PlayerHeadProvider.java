package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public enum PlayerHeadProvider implements IBlockComponentProvider {

    INSTANCE;

    static final ItemStack PLAYER_HEAD_STACK = new ItemStack(Items.PLAYER_HEAD);

    @Nullable
    @Override
    public ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        SkullBlockEntity skull = accessor.getBlockEntity();
        if (skull != null && skull.getOwnerProfile() != null) {
            var tag = PLAYER_HEAD_STACK.getOrCreateTag();
            var skullOwner = tag.getCompound("SkullOwner");
            tag.put("SkullOwner", NbtUtils.writeGameProfile(skullOwner, skull.getOwnerProfile()));
            return new ItemComponent(PLAYER_HEAD_STACK);
        }
        return null;
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        SkullBlockEntity skull = accessor.getBlockEntity();
        if (skull == null) return;
        var profile = skull.getOwnerProfile();
        if (profile == null) return;
        var name = profile.getName();
        if (name == null || StringUtils.isBlank(name)) return;
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, IWailaConfig.get().getFormatter().blockName(I18n.get("block.minecraft.player_head.named", name)));
    }

}
