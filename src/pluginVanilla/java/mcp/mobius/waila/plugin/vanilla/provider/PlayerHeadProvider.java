package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
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
            PLAYER_HEAD_STACK.set(DataComponents.PROFILE, skull.getOwnerProfile());
            return new ItemComponent(PLAYER_HEAD_STACK);
        }
        return null;
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.PLAYER_HEAD_NAME)) {
            SkullBlockEntity skull = accessor.getBlockEntity();
            if (skull != null && skull.getOwnerProfile() != null && !StringUtils.isBlank(skull.getOwnerProfile().gameProfile().getName())) {
                tooltip.addLine(Component.translatable(skull.getOwnerProfile().gameProfile().getName()));
            }
        }
    }

}
