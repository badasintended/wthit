package mcp.mobius.waila.plugin.vanilla.component;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;

/**
 * Since Mojang decided to hardcode the boat type using {@link Boat.Type},
 * mods will use one of the enum and override the {@link Boat#getPickResult()}.
 * This causes the core component method unreliable resulting the object name showing as {@code Minecraft}.
 * <p>
 * For that, we'll be using the item name instead.
 */
public enum BoatComponent implements IEntityComponentProvider {

    INSTANCE;

    ItemStack stack;

    @Override
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        stack = accessor.getEntity().getPickResult();
        IWailaConfig.Formatting formatting = IWailaConfig.get().getFormatting();
        tooltip.set(WailaConstants.OBJECT_NAME_TAG, new TextComponent(formatting.formatEntityName(stack.getHoverName().getString())));

        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
            tooltip.set(WailaConstants.REGISTRY_NAME_TAG, new TextComponent(formatting.formatRegistryName(Registry.ITEM.getKey(stack.getItem()))));
        }
    }

    @Override
    public void appendTail(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            tooltip.set(WailaConstants.MOD_NAME_TAG, new TextComponent(IWailaConfig.get().getFormatting().formatModName(IModInfo.get(stack.getItem()).getName())));
        }
    }

}
